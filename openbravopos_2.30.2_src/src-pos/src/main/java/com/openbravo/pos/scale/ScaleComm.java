//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.scale;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4jFactory;

public class ScaleComm implements Scale, SerialPortEventListener {
    
	private Log log = Log4jFactory.getLog(ScaleComm.class);
	
    private String m_sPortScale;
    private CommPortIdentifier m_PortIdPrinter;
    private SerialPort m_CommPortPrinter;      
    private OutputStream m_out;
    private InputStream m_in;

    private static final int SCALE_READY = 0;
    private static final int SCALE_READING = 1;
    
    private double m_dWeightBuffer;
    private int m_iStatusScale;
        
    /** Creates a new instance of ScaleComm */
    public ScaleComm(String sPortPrinter) {
        
    	log.info(" inicializando objeto bascula ");
    	
    	m_sPortScale = sPortPrinter;
        m_out = null;
        m_in = null;
        
        m_iStatusScale = SCALE_READY; 
        m_dWeightBuffer = 0.0;
    }
    
    public Double readWeight() {
        
    	log.warn(" intentando leer el peso ");
    	
        synchronized(this) {

            if (m_iStatusScale != SCALE_READY) {
            	log.warn(" leemos que la escala no esta aun preparada ");
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                	log.error(" error en la excepcion" ,e);
                }
                if (m_iStatusScale != SCALE_READY) {
                    // bascula tonta.
                	log.info(" esperamos pero no entendemos que este preparada, que no se configura bien");
                    m_iStatusScale = SCALE_READY;
                }
            }
            
            // Ya estamos en SCALE_READY
            m_dWeightBuffer = 0.0;
            write(new byte[] {0x05});
            flush();             
            
            // Esperamos un ratito
            try {
                wait(1000);
            } catch (InterruptedException e) {
            	log.error(" error enviando datos a la balanza ", e);
            }
            
            if (m_iStatusScale == SCALE_READY) {
                // a value as been readed.
            	log.warn(" algo hemos leido " + m_dWeightBuffer);
                double dWeight = m_dWeightBuffer / 1000.0;
                m_dWeightBuffer = 0.0;
                return new Double(dWeight);
            } else {
            	log.warn(" la balanza no estaba preparada, devolvemos un 0");
                m_iStatusScale = SCALE_READY;
                m_dWeightBuffer = 0.0;
                return new Double(0.0);
            }
        }
    }
    
    private void flush() {
    	log.warn(" flush de los datos en el puerto ");
        try {
            m_out.flush();
        } catch (IOException e) {
        	log.error(" error enviando a la balanza datos ", e);
        }        
    }
    
    private void write(byte[] data) {
    	log.warn(" enviando datos " + new String(data));
        try {  
            if (m_out == null) {
                m_PortIdPrinter = CommPortIdentifier.getPortIdentifier(m_sPortScale); // Tomamos el puerto                   
                m_CommPortPrinter = (SerialPort) m_PortIdPrinter.open("PORTID", 2000); // Abrimos el puerto       

                m_out = m_CommPortPrinter.getOutputStream(); // Tomamos el chorro de escritura   
                m_in = m_CommPortPrinter.getInputStream();
                
                m_CommPortPrinter.addEventListener(this);
                m_CommPortPrinter.notifyOnDataAvailable(true);
                
                //m_CommPortPrinter.setSerialPortParams(4800, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_ODD); // Configuramos el puerto
                m_CommPortPrinter.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_ODD); // Configuramos el puerto
            }
            m_out.write(data);
        } catch (NoSuchPortException e) {
            log.error(" error en el puerto ", e);
        } catch (PortInUseException e) {
        	log.error(" puerto en uso ", e);
        } catch (UnsupportedCommOperationException e) {
            log.error(" error en la comunicacion con la operacion ", e);
        } catch (TooManyListenersException e) {
            log.error(" demasiados listeners ", e);
        } catch (IOException e) {
            log.error(" error I/O ", e);
        }        
    }
    
    public void serialEvent(SerialPortEvent e) {

    	log.warn("tipo de evento de puerto serie: " + e.getEventType());
    	
	// Determine type of event.
	switch (e.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                try {
                	
                	log.warn(" available " + m_in.available());
                	
                    while (m_in.available() > 0) {
                        int b = m_in.read();
                        log.warn(" leidos " + b + " datos de la balanza ");
                        if (b == 0x001E) { // RS ASCII
                            // Fin de lectura
                            synchronized (this) {
                                m_iStatusScale = SCALE_READY;
                                notifyAll();
                            }
                        } else if (b > 0x002F && b < 0x003A){
                            synchronized(this) {
                                if (m_iStatusScale == SCALE_READY) {
                                    m_dWeightBuffer = 0.0; // se supone que esto debe estar ya garantizado
                                    m_iStatusScale = SCALE_READING;
                                }
                                m_dWeightBuffer = m_dWeightBuffer * 10.0 + b - 0x0030;
                            }
                        } else {
                            // caracteres invalidos, reseteamos.
                            m_dWeightBuffer = 0.0; // se supone que esto debe estar ya garantizado
                            m_iStatusScale = SCALE_READY;
                        }
                    }

                } catch (IOException eIO) {
                	log.error(" error en la lectura ", eIO);
                }
                break;
        }

    }       
}

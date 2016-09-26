package com.openbravo.pos.scale;

import gnu.io.*;

import java.io.*;
import java.util.TooManyListenersException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4jFactory;

public class ScaleSamsungEsp2 implements Scale, SerialPortEventListener {
    
	private Log log = Log4jFactory.getLog(ScaleSamsungEsp2.class);
	
    private CommPortIdentifier m_PortIdPrinter;
    private SerialPort m_CommPortPrinter;  
    
    private String m_sPortScale;
    private OutputStream m_out;
    private InputStream m_in;

    private static final int SCALE_READY = 0;
    private static final int SCALE_READING = 1;
    private static final int SCALE_READINGDECIMALS = 2;
    
    private double m_dWeightBuffer;
    private double m_dWeightDecimals;
    private int m_iStatusScale;
        
    /** Creates a new instance of ScaleComm */
    public ScaleSamsungEsp2(String sPortPrinter) {
        m_sPortScale = sPortPrinter;
        m_out = null;
        m_in = null;
        
        m_iStatusScale = SCALE_READY; 
        m_dWeightBuffer = 0.0;
        m_dWeightDecimals = 1.0;
    }
    
    public Double readWeight() {
        
        synchronized(this) {

            if (m_iStatusScale != SCALE_READY) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                }
                if (m_iStatusScale != SCALE_READY) {
                    // bascula tonta.
                    m_iStatusScale = SCALE_READY;
                }
            }
            
            // Ya estamos en SCALE_READY
            m_dWeightBuffer = 0.0;
            m_dWeightDecimals = 1.0;
            write(new byte[] {0x24}); // N
            log.warn(" enviando datos " + new String(new byte[] {0x24}));
            flush();             
            
            // Esperamos un ratito
            try {
                wait(1000);
            } catch (InterruptedException e) {
            	log.error(" interrupcion incorrecta ", e);
            }
            
            if (m_iStatusScale == SCALE_READY) {
                // hemos recibido cositas o si no hemos recibido nada estamos a 0.0
            	
                double dWeight = m_dWeightBuffer / m_dWeightDecimals;
                m_dWeightBuffer = 0.0;
                m_dWeightDecimals = 1.0;
                return new Double(dWeight);
            } else {
            	log.warn(" Devolvemos datos 0 ----");
                m_iStatusScale = SCALE_READY;
                m_dWeightBuffer = 0.0;
                m_dWeightDecimals = 1.0;
                return new Double(0.0);
            }
        }
    }
    
    private void flush() {
        try {
            m_out.flush();
        } catch (IOException e) {
        }        
    }
    
    private void write(byte[] data) {
        try {  
            if (m_out == null) {
                m_PortIdPrinter = CommPortIdentifier.getPortIdentifier(m_sPortScale); // Tomamos el puerto                   
                m_CommPortPrinter = (SerialPort) m_PortIdPrinter.open("PORTID", 2000); // Abrimos el puerto       

                m_out = m_CommPortPrinter.getOutputStream(); // Tomamos el chorro de escritura   
                m_in = m_CommPortPrinter.getInputStream();
                
                m_CommPortPrinter.addEventListener(this);
                m_CommPortPrinter.notifyOnDataAvailable(true);
                
                m_CommPortPrinter.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE); // Configuramos el puerto
            }
            m_out.write(data);
            log.warn(" enviados datos " + new String(data));
        } catch (NoSuchPortException e) {
            e.printStackTrace();
        } catch (PortInUseException e) {
            e.printStackTrace();
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    public void serialEvent(SerialPortEvent e) {

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
                	
                	log.warn(" leyendo datos samsungesp2 " + m_in.available());
                	
                    while (m_in.available() > 0) {
                        int b = m_in.read();
                        
                        if (b == 0x000D) { // CR ASCII
                            // Fin de lectura
                            synchronized (this) {
                                m_iStatusScale = SCALE_READY;
                                notifyAll();
                            }
                        } else if ((b > 0x002F && b < 0x003A) || b == 0x002E){
                            synchronized(this) {
                                if (m_iStatusScale == SCALE_READY) {
                                    m_dWeightBuffer = 0.0; // se supone que esto debe estar ya garantizado
                                    m_dWeightDecimals = 1.0;
                                    m_iStatusScale = SCALE_READING;
                                }
                                if (b == 0x002E) {
                                    m_iStatusScale = SCALE_READINGDECIMALS;
                                } else {
                                    m_dWeightBuffer = m_dWeightBuffer * 10.0 + b - 0x0030;
                                    if (m_iStatusScale == SCALE_READINGDECIMALS) {
                                        m_dWeightDecimals *= 10.0;
                                    }
                                }
                            }
                        } else {
                            // caracteres invalidos, reseteamos.
                            m_dWeightBuffer = 0.0; // se supone que esto debe estar ya garantizado
                            m_dWeightDecimals = 1.0;
                            m_iStatusScale = SCALE_READY;
                        }
                    }

                } catch (IOException eIO) {
                	log.error(" error io ", eIO);
                	
                }
                break;
        }
    }       
}
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

public class ScaleGramAsep15 implements Scale, SerialPortEventListener {
    
	private Log log = Log4jFactory.getLog(ScaleGramAsep15.class);
	
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
    public ScaleGramAsep15(String sPortPrinter) {
        
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

            //write(new byte[] {0x24});
            //write(new byte[] {0x24}); // $
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
            	log.warn(" m_out es nulo, inicializamos ");
                m_PortIdPrinter = CommPortIdentifier.getPortIdentifier(m_sPortScale); // Tomamos el puerto                   
                m_CommPortPrinter = (SerialPort) m_PortIdPrinter.open("PORTID", 2000); // Abrimos el puerto       

                m_out = m_CommPortPrinter.getOutputStream(); // Tomamos el chorro de escritura   
                m_in = m_CommPortPrinter.getInputStream();
                
                m_CommPortPrinter.addEventListener(this);
                m_CommPortPrinter.notifyOnDataAvailable(true);
                
                //m_CommPortPrinter.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_ODD); 
                m_CommPortPrinter.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE); 
            }
            log.warn(" enviados datos " + new String(data));
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
                    while (m_in.available() > 0) {
                    	log.warn(" available " + m_in.available());
                        int b = m_in.read();
                        log.warn(" leido character " + Character.toString ((char) b) + " available " + m_in.available());
                        
                        if (b == 0x001E) { // RS ASCII
                            // Fin de lectura
                        	log.warn(" fin de la lectura en el caracter ASCII " + Character.toString ((char) b));
                            synchronized (this) {
                                m_iStatusScale = SCALE_READY;
                                notifyAll();
                            }
                        } else { // if (b > 0x002F && b < 0x003A){
                        	log.warn(" salimos de la lectura " + Character.toString ((char) b));
                            synchronized(this) {
                                if (m_iStatusScale == SCALE_READY) {
                                    m_dWeightBuffer = 0.0; // se supone que esto debe estar ya garantizado
                                    m_iStatusScale = SCALE_READING;
                                }
                                m_dWeightBuffer = m_dWeightBuffer * 10.0 + b - 0x0030;
                                log.warn(" m_dWeightBuffer " + m_dWeightBuffer);
                            }
                       /* } else {
                           
                        	log.warn(" reseteando porque vienen caracteres invalidos " + String.valueOf(b));
                        	
                        	m_dWeightBuffer = 0.0; // se supone que esto debe estar ya garantizado
                            m_iStatusScale = SCALE_READY;
                        } */
                    }
                    }

                } catch (IOException eIO) {
                	log.error(" error en la lectura ", eIO);
                }
                break;
        }

    }   
    
    
  /*  public void serialEvent(SerialPortEvent event){
       
    	String ok = new String();
    	switch(event.getEventType()) {
        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            log.warn(" vacio esta el buffer ");
            break;

        case SerialPortEvent.DATA_AVAILABLE:
            byte[] readBuffer = new byte[20];
            try {
                 int numBytes = m_in.read(readBuffer);
                 log.warn(" leidos " + numBytes);
                 String str = new String (readBuffer, "UTF-8");
                 ok+=str;
                 //System.out.print(str);
                 System.out.println(" lectura str " + str);
                 System.out.print(ok.replaceAll("\\s+", ""));
                 //System.out.print(str.replace("\n", "").replace("\r", ""));
                } catch (IOException ex) {}
            break;

    } 
    }*/
}
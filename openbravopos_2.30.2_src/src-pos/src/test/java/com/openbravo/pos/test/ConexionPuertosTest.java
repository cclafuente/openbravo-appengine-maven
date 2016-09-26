package com.openbravo.pos.test;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;

import junit.framework.TestCase;

import com.openbravo.pos.scale.ScaleComm;


public class ConexionPuertosTest extends TestCase {

	
	public void testCargarNativa(){
	try{
		System.loadLibrary("rxtxSerial");
		System.out.println("Se ha cargado la libreria nativa correctamente");
		} catch (UnsatisfiedLinkError u) {
		System.err.println("No se ha encontrado la libreria nativa de puerto serie");
		}
	}
	
	
	public void testScale(){
		ScaleComm scale = new ScaleComm("COM4");
		
		System.out.println(" Leer peso: " + scale.readWeight());
		
		
	}
	
	public void testListaPuertos(){
		
		System.out.println(" Leyendo lista de puertos .....");
		
		Enumeration listaPuertos = CommPortIdentifier.getPortIdentifiers();
		
		 CommPortIdentifier idPuerto = null;
		
		 boolean encontrado = false;
		
		 while (listaPuertos.hasMoreElements() && !encontrado) {
		
		 idPuerto = (CommPortIdentifier) listaPuertos.nextElement();
		
		 if (idPuerto.getPortType() == CommPortIdentifier.PORT_SERIAL) {
		
		 if (idPuerto.getName().equals("COM1")) {
		
		 encontrado = true;
		 System.out.println(" Encontrado puerto COM1");
		
		 }else{
			 System.out.println(" Puerto : " + idPuerto.getName());
		 }
	
	 }
		
	 }
	}

}

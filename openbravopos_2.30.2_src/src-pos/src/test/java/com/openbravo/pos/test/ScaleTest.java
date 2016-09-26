package com.openbravo.pos.test;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4jFactory;

import com.openbravo.pos.scale.ScaleSamsungEsp;

public class ScaleTest extends TestCase {
	
	private Log log = Log4jFactory.getLog(ScaleTest.class);
	
	/*public void testLeer() throws Exception{
		ScaleGramAsep15 scale = new ScaleGramAsep15("COM4");
		System.out.println(" leyendo peso " + scale.readWeight());
		System.out.println(" Empezamos segunda lectura ********************************************");
		System.out.println(" leyendo peso 2 " + scale.readWeight());
	}*/
	
	public void testLeer2() throws Exception{
		ScaleSamsungEsp scale = new ScaleSamsungEsp("COM4");
		System.out.println(" leyendo peso " + scale.readWeight());
	}
	
	/*public void testLeer3() throws Exception{
		ScaleSamsungEsp2 scale = new ScaleSamsungEsp2("COM4");
		System.out.println(" leyendo peso " + scale.readWeight());
	}*/
	
	/*public void testScaleComm()throws Exception{
		ScaleComm scale = new ScaleComm("COM4");
		System.out.println(" leyendo peso en " + scale.readWeight());
	}*/

}

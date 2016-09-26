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

import java.awt.Component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.util.StringParser;

public class DeviceScale {
    
	Log log = LogFactory.getLog(DeviceScale.class);
	
    private Scale m_scale;
    
    /** Creates a new instance of DeviceScale */
    public DeviceScale(Component parent, AppProperties props) {
    	
    	log.warn(" Inicializando DeviceScale ");
    	
        StringParser sd = new StringParser(props.getProperty("machine.scale"));
        String sScaleType = sd.nextToken(':');
        String sScaleParam1 = sd.nextToken(',');
        // String sScaleParam2 = sd.nextToken(',');
        
        if ("dialog1".equals(sScaleType)) {
            m_scale = new ScaleComm(sScaleParam1);
        } else if ("samsungesp".equals(sScaleType)) {
            m_scale = new ScaleSamsungEsp(sScaleParam1);    
        }else if ("gramasep15".equals(sScaleType)){
        	m_scale = new ScaleGramAsep15(sScaleParam1);
        } else if ("fake".equals(sScaleType)) { // a fake scale for debugging purposes
            m_scale = new ScaleFake();            
        } else if ("screen".equals(sScaleType)) { // on screen scale
            m_scale = new ScaleDialog(parent);
        } else {
            m_scale = null;
        }
        
        log.info(" inicializada la balanza " + sScaleType + " " + sScaleParam1 + " de clase " + m_scale.getClass().getSimpleName());
    }
    
    public boolean existsScale() {
        return m_scale != null;
    }
    
    public Double readWeight() throws ScaleException {
        
        if (m_scale == null) {
            throw new ScaleException(AppLocal.getIntString("scale.notdefined"));
        } else {
            Double result = m_scale.readWeight();
            if (result == null) {
                return null; // Canceled by the user / scale
            } else if (result.doubleValue() < 0.002) {
            	log.warn(" parece que no leemos nada de la balanza " + result.doubleValue());
                // invalid result. nothing on the scale
                throw new ScaleException(AppLocal.getIntString("scale.invalidvalue"));                
            } else {
                // valid result
            	log.warn(" resultado en la balanza " + result);
                return result;
            }
        }
    }    
}

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

package com.openbravo.pos.sales;

import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4jFactory;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.PreparedSentence;
import com.openbravo.data.loader.SerializerReadBasic;
import com.openbravo.data.loader.SerializerReadClass;
import com.openbravo.data.loader.SerializerWriteBasicExt;
import com.openbravo.data.loader.SerializerWriteString;
import com.openbravo.data.loader.Session;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.forms.BeanFactoryDataSingle;
import com.openbravo.pos.ticket.TicketInfo;

/**
 *
 * @author adrianromero
 */
public class DataLogicReceipts extends BeanFactoryDataSingle {
    
	private Log log = Log4jFactory.getLog(DataLogicReceipts.class);
	
    private Session s;
    
    /** Creates a new instance of DataLogicReceipts */
    public DataLogicReceipts() {
    }
    
    public void init(Session s){
        this.s = s;
    }
     
    public final TicketInfo getSharedTicket(String Id) throws BasicException {
        TicketInfo shared = null;
        if (Id == null) {
            return null; 
        } else {
            Object[]record = (Object[]) new StaticSentence(s
                    , "SELECT CONTENT FROM SHAREDTICKETS WHERE ID = ?"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.SERIALIZABLE})).find(Id);
            shared = ( record == null ? null : (TicketInfo) record[0]);
        }
        
        if (!(shared == null)){
        	log.warn(" ticket compartido " + shared.getId());
        	log.warn(" customer id del ticket compartido " + shared.getCustomerId());
        	if (shared.getUser() != null){
        		log.warn(" user del ticket " + shared.getUser().getId());
        		log.warn(shared.getUser().toString());
        	}
        	//TODO solo para probar la carga de clientes, eliminar de inmediato
        	//shared.setCustomer(new CustomerInfoExt("f7cd4b3b-af43-4924-9c58-e644700051ca"));
        	log.warn(ReflectionToStringBuilder.toString(shared, ToStringStyle.MULTI_LINE_STYLE));
        	
        }
        return shared;
    } 
    
    public final List<SharedTicketInfo> getSharedTicketList() throws BasicException {
        
        return (List<SharedTicketInfo>) new StaticSentence(s
                , "SELECT ID, NAME FROM SHAREDTICKETS ORDER BY ID"
                , null
                , new SerializerReadClass(SharedTicketInfo.class)).list();
    }
    
    public final void updateSharedTicket(final String id, final TicketInfo ticket) throws BasicException {
         
        Object[] values = new Object[] {id, ticket.getName(), ticket};
        Datas[] datas = new Datas[] {Datas.STRING, Datas.STRING, Datas.SERIALIZABLE};
        new PreparedSentence(s
                , "UPDATE SHAREDTICKETS SET NAME = ?, CONTENT = ? WHERE ID = ?"
                , new SerializerWriteBasicExt(datas, new int[] {1, 2, 0})).exec(values);
    }
    
    public final void insertSharedTicket(final String id, final TicketInfo ticket) throws BasicException {
        
    	log.warn(" guardando/insertando ticket compartido ");
    	log.warn(ReflectionToStringBuilder.toString(ticket, ToStringStyle.MULTI_LINE_STYLE));
    	
        Object[] values = new Object[] {id, ticket.getName(), ticket};
        Datas[] datas = new Datas[] {Datas.STRING, Datas.STRING, Datas.SERIALIZABLE};
        
        new PreparedSentence(s
            , "INSERT INTO SHAREDTICKETS (ID, NAME,CONTENT) VALUES (?, ?, ?)"
            , new SerializerWriteBasicExt(datas, new int[] {0, 1, 2})).exec(values);
    }
    
    public final void deleteSharedTicket(final String id) throws BasicException {

        new StaticSentence(s
            , "DELETE FROM SHAREDTICKETS WHERE ID = ?"
            , SerializerWriteString.INSTANCE).exec(id);      
    }    
}

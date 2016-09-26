/*
 * Copyright (C) 2014 Klaus Reimer <k@ailis.de>
 * See LICENSE.txt for licensing information.
 */

package com.openbravo.pos.test;


import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import junit.framework.TestCase;

import org.usb4java.ConfigDescriptor;
import org.usb4java.Context;
import org.usb4java.DescriptorUtils;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

/**
 * Simply lists all available USB devices.
 * 
 * @author Klaus Reimer <k@ailis.de>
 */
public class UsbToJavaTest extends TestCase{
    /**
     * Main method.
     * 
     * @param args
     *            Command-line arguments (Ignored)
     */
    public void testLeer()
    {
    	
    	String[] args;
    	// Create the libusb context
        final Context context = new Context();

        // Initialize the libusb context
        int result = LibUsb.init(context);
        if (result < 0)
        {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        // Read the USB device list
        final DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(context, list);
        System.out.println(" Number of readed usb devices: " + result);
        if (result < 0)
        {
            throw new LibUsbException("Unable to get device list", result);
        }

        try
        {
            // Iterate over all devices and dump them
            for (Device device: list)
            {
                dumpDevice(device);
            }
        }
        finally
        {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }
        
        // Deinitialize the libusb context
        LibUsb.exit(context);
    
    }
    
    /*
     * 
     *  DeviceHandle handle = new DeviceHandle();
        result = LibUsb.open(device, handle);
        if (result < 0)
        {
            System.out.println(String.format("Unable to open device: %s. "
                + "Continuing without device handle.",
                LibUsb.strError(result)));
            handle = null;
        }

        // Dump the device descriptor
        System.out.print(descriptor.dump(handle));

        // Dump all configuration descriptors
        dumpConfigurationDescriptors(device, descriptor.bNumConfigurations());
     * 
     * 
     */
    
    public static void dumpConfigurationDescriptors(final Device device,
            final int numConfigurations)
        {
            for (byte i = 0; i < numConfigurations; i += 1)
            {
                final ConfigDescriptor descriptor = new ConfigDescriptor();
                final int result = LibUsb.getConfigDescriptor(device, i, descriptor);
                if (result < 0)
                {
                    throw new LibUsbException("Unable to read config descriptor",
                        result);
                }
                try
                {
                    System.out.println(descriptor.dump().replaceAll("(?m)^",
                        "  "));
                }
                finally
                {
                    // Ensure that the config descriptor is freed
                    LibUsb.freeConfigDescriptor(descriptor);
                }
            }
        }
    
    
    public static void dumpDevice(final Device device)
    {
        // Dump device address and bus number
        final int address = LibUsb.getDeviceAddress(device);
        final int busNumber = LibUsb.getBusNumber(device);
        
        if ((busNumber == 1)&&(address == 2)){
        	System.out.println(" Leyendo puerto de la bascula: ");
        	
        	//dumpDevice_old(device);
        	sendRequest(device);
        }
        
        System.out.println(String
            .format("Device %03d/%03d", busNumber, address));
    }
    
    
    
    public static void dumpDevice_old(final Device device)
    {
        // Dump device address and bus number
        final int address = LibUsb.getDeviceAddress(device);
        final int busNumber = LibUsb.getBusNumber(device);
        System.out.println(String
            .format("Device %03d/%03d", busNumber, address));

        // Dump port number if available
        final int portNumber = LibUsb.getPortNumber(device);
        
        if (portNumber != 0)
            System.out.println("Connected to port: " + portNumber);

        // Dump parent device if available
        final Device parent = LibUsb.getParent(device);
        if (parent != null)
        {
            final int parentAddress = LibUsb.getDeviceAddress(parent);
            final int parentBusNumber = LibUsb.getBusNumber(parent);
            System.out.println(String.format("Parent: %03d/%03d",
                parentBusNumber, parentAddress));
        }

        // Dump the device speed
        System.out.println("Speed: "
            + DescriptorUtils.getSpeedName(LibUsb.getDeviceSpeed(device)));

        // Read the device descriptor
        final DeviceDescriptor descriptor = new DeviceDescriptor();
        int result = LibUsb.getDeviceDescriptor(device, descriptor);
        if (result < 0)
        {
            throw new LibUsbException("Unable to read device descriptor",
                result);
        }

        // Try to open the device. This may fail because user has no
        // permission to communicate with the device. This is not
        // important for the dumps, we are just not able to resolve string
        // descriptor numbers to strings in the descriptor dumps.
        DeviceHandle handle = new DeviceHandle();
        result = LibUsb.open(device, handle);
        if (result < 0)
        {
            System.out.println(String.format("Unable to open device: %s. "
                + "Continuing without device handle.",
                LibUsb.strError(result)));
            handle = null;
        }

        // Dump the device descriptor
        System.out.print(descriptor.dump(handle));

        // Dump all configuration descriptors
        dumpConfigurationDescriptors(device, descriptor.bNumConfigurations());

        // Close the device if it was opened
        if (handle != null)
        {
            LibUsb.close(handle);
        }
    }
    
    
    public static void sendRequest(Device device){
        DeviceHandle handle = new DeviceHandle();        
        int result = LibUsb.open(device, handle);
        if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to open USB device", result);
        try
        {

            ByteBuffer buffer = ByteBuffer.allocateDirect(8);

            buffer.put(new byte[] { -86, -12, -12, -12, -12, -12, -12, -12 });
            int transfered = LibUsb.controlTransfer(handle,
                    (byte) (LibUsb.REQUEST_TYPE_CLASS),
                    (byte) 0x09, (short) 0x200, (short) 1, buffer, 1000);
            if (transfered < 0) throw new LibUsbException("Control transfer failed", transfered);
            System.out.println(transfered + " bytes sent");

            ByteBuffer read_buffer = ByteBuffer.allocateDirect(8);
            IntBuffer read_transfered = IntBuffer.allocate(1);
            result = LibUsb.interruptTransfer(handle, (byte) 0x81, read_buffer, read_transfered, 2000);

            if (result != LibUsb.SUCCESS)
                throw new LibUsbException("Error while reading", result);            
        }
        finally
        {
            LibUsb.close(handle);
        }
    }


}



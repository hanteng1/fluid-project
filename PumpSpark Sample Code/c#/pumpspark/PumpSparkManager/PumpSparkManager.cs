/*
    PumpSparkManager
    Microsoft Research
    UIST 2013 Student Innovation Contest
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO.Ports;
using System.Windows;
using System.Windows.Forms;

namespace PumpSpark
{
    public class PumpSparkManager
    {
        // Create a new SerialPort object with default settings.
        static SerialPort _serialPort = new SerialPort();        

        // Configure the serial port and specify COM Port
        public void ConfigurePort(string portName)
        {            
            // Allow the user to set the appropriate portName (e.g. "COM1")
            _serialPort.PortName = portName;

            // Set appropriate properties (do not change these)            
            _serialPort.BaudRate = 9600;
            _serialPort.Parity = (Parity)Enum.Parse(typeof(Parity), "None");
            _serialPort.DataBits = 8;
            _serialPort.StopBits = (StopBits)Enum.Parse(typeof(StopBits), "One");
            _serialPort.Handshake = (Handshake)Enum.Parse(typeof(Handshake), "None");

            // Set the read/write timeouts
            _serialPort.ReadTimeout = 500;
            _serialPort.WriteTimeout = 500;
        }

        // Open the serial port
        public void ConnectPort()
        {
            try
            {
                _serialPort.Open();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                MessageBox.Show(ex.Message);
                return;
            }
        }

        // Close the serial port
        public void DisconnectPort()
        {
            try
            {
                _serialPort.Close();
            }
            catch (Exception ex)
            {
                //Console.WriteLine("Exception on serial port close");
                //MessageBox.Show(ex.Message);
                return;
            }
        }

        /* 
           ActuatePump(byte pumpNumber, byte flowValue)
           Method to actuate pumps connected to the PumpSpark board
           pumpNumber => {0, 1, 2, 3, 4, 5, 6, 7}
           flowValue  => {min to max} = {0 to 254} 
        */
        public void ActuatePump(byte pumpNumber, byte flowValue)
        {
            if (_serialPort.IsOpen == false)
            {
                ConnectPort();
            }

            byte[] start = new byte[] { 255 };
            byte[] pump = new byte[] { pumpNumber };
            byte[] flow = new byte[] { flowValue };

            try
            {                
                _serialPort.Write(start, 0, 1);
                _serialPort.Write(pump, 0, 1);
                _serialPort.Write(flow, 0, 1);
            }
            catch (Exception ex)
            {
                //Console.WriteLine("Exception on serial port write");
                //MessageBox.Show(ex.Message);
                return;
            }
        }
    }
}
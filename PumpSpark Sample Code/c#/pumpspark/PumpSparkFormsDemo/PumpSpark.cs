/*
    PumpSpark Demo
    Microsoft Research
    UIST 2013 Student Innovation Contest
 */

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Threading;
using PumpSpark;

namespace PumpSparkFormsDemo
{
    public partial class PumpSpark : Form
    {
        // Instantiate a new PumpSparkManager
        PumpSparkManager pumpSpark = new PumpSparkManager();

        public PumpSpark()
        {
            InitializeComponent();
            
            // Configure serial port and specify COM Port
            pumpSpark.ConfigurePort("COM9");

            // Open the serial port
            pumpSpark.ConnectPort();

            // Looping through 10x times
            for (int i = 0; i < 10; i++)
            {
                // Output message to console
                Console.WriteLine("Actuate pump 0 at flow 254");

                // Actuating pump 0 at max value of 254 for 5 seconds
                pumpSpark.ActuatePump(0, 254);
                Thread.Sleep(5000);

                // Output message to console
                Console.WriteLine("Actuate pump 0 at flow 0");

                // Actuating pump 0 at min value of 0 for 5 seconds
                pumpSpark.ActuatePump(0, 0);
                Thread.Sleep(5000);
            }

            pumpSpark.DisconnectPort();
        }

        private void PumpSpark_FormClosing(object sender, FormClosingEventArgs e)
        {
            pumpSpark.DisconnectPort();
        }
    }
}

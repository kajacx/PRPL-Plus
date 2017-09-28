using ICSharpCode.SharpZipLib.GZip;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PRPLDatGZip
{
    class Program
    {
        static void Main(string[] args)
        {
            if(args.Length < 2)
            {
                Console.WriteLine("Expected arguments: fileFrom fileTo [-b64]");
                return;
            }

            bool useBase64 = false;

            for(int i = 2; i<args.Length; i++)
            {
                if(args[i].Equals("-b64"))
                {
                    useBase64 = true;
                }
            }

            string fileFrom = args[0];
            string fileTo = args[1];

            byte[] data;
            if(useBase64)
            {
                string text = File.ReadAllLines(fileFrom)[0];
                data = Convert.FromBase64String(text);
            } else
            {
                data = File.ReadAllBytes(fileFrom);
            }

            Stream stream = new FileStream(fileTo, FileMode.Create, FileAccess.Write);

            BinaryWriter writer = new BinaryWriter(stream);
            writer.Write(data.Length);
            //stream.Close();

            GZipOutputStream gZipOutputStream = new GZipOutputStream(stream);
            gZipOutputStream.SetLevel(5);
            gZipOutputStream.Write(data, 0, data.Length);
            gZipOutputStream.Close();
        }
    }
}

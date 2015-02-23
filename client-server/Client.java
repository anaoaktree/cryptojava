/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.io.*;

public class Client {
    public static void main(String args[])
    {
        Socket s;
        BufferedReader in;
        PrintWriter out;
        String str = "", str2;
        Scanner stdin = new Scanner(System.in);
        
        try
        {
            s = new Socket("localhost", 4567);
            
            try
            {
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new PrintWriter(s.getOutputStream());
                
                System.out.println("Connected to server");
                
                while(!(str.equals("exit")))
                {
                    str = stdin.nextLine();
                    out.println(str);
                    out.flush();
                }
                
                try
                {
                    in.close();
                    out.close();
                    s.close();
                    System.out.println("Disconnected from server");
                }
                catch(IOException e)
                {
                    System.out.println("*** Error while exiting ***");
                }
            }
            catch(IOException e)
            {
                System.out.println("*** Failed to make I/O with the server ***");
            }
        }
        catch(IOException e)
        {
            System.out.println("*** Failed to connect to server ***");
        }
    }
    
    
    
}

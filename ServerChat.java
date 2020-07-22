package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

public class ServerChat {

    ArrayList users;

    public void go ()
    {
        users=new ArrayList();
        try {
            ServerSocket serverSocket = new ServerSocket(5001);
            System.out.println("Server is active. Waiting for clients...");

            while (true)
            {
                Socket client = serverSocket.accept();

                PrintWriter printWriter=new PrintWriter(client.getOutputStream());
                users.add(printWriter);

                Thread sendAllClientsThread= new Thread(new ClientHandler(client));
                sendAllClientsThread.start();

                //check all active threads
                Thread.getAllStackTraces().keySet().forEach((t) -> System.out.println(t.getName() + "\nIs Daemon " + t.isDaemon() + "\nIs Alive " + t.isAlive()));
            }

        }catch (IOException e)
        {
            System.out.println("ServerChat: error while creating server socket");
            e.printStackTrace();
        }
    }//end method go()

    public void sendEveryone (String message)
    {
        Iterator iterator=users.iterator();
        while (iterator.hasNext()){
            PrintWriter clientWrite= (PrintWriter) iterator.next();
            clientWrite.println(message);
            clientWrite.flush();
        }

    }//end method sendEveryone()


    public class ClientHandler implements Runnable{
        Socket socket;
        BufferedReader bufferedReader;

        ClientHandler(Socket socket)
        {
            try {
                this.socket = socket;
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
            }catch (IOException e){
                e.printStackTrace();
                System.out.println("Server Chat/Client Handler: Error while creating socket");
            }
        }
        @Override
        public void run() {
            String message;
            try {
                if (!socket.isInputShutdown())
                while ((message = bufferedReader.readLine()) != null) {
                    System.out.println(message);
                    sendEveryone(message);
                }
                else {
                    //sendEveryone("Log out!");
                    bufferedReader.close();
                    socket.close();

                }
            }catch (SocketException e){

            }catch (IOException e)
            {
                e.printStackTrace();
                System.out.println("Server Chat/Client Handler: Error while reading message from client");
            }

        }
    }//end inner class

}

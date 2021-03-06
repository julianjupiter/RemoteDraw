package com.starea.thread;

import com.starea.converter.DrawingObjectsToStringConverter;
import com.starea.datamodel.Infrastructure;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.Inflater;

public class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;

    public WriteThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            if(!getSocket().isClosed()) {
                System.out.println(socket.getLocalPort());
                try {
                    OutputStream output = socket.getOutputStream();
                    writer = new PrintWriter(output, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String protocol;

                while (true) {
                    protocol = Infrastructure.getInstance().getProtocol();
                    while (protocol == null) {
                        if (Infrastructure.getInstance().getConnectionState() != null && Infrastructure.getInstance().getConnectionState().equals("Closed")) {
                            break;
                        }
                        protocol = Infrastructure.getInstance().getProtocol();
                    }
                    if (Infrastructure.getInstance().getConnectionState() != null && Infrastructure.getInstance().getConnectionState().equals("Closed")) {
                        break;
                    }
                    if (protocol != null && protocol.equals("INVITE")) {
                        writer.println(protocol + ":" + Infrastructure.getInstance().getData());
                        System.out.println(Infrastructure.getInstance().getData());
                    }

                    if (protocol != null && protocol.equals("JOIN")) {
                        writer.println(protocol + ":" + Infrastructure.getInstance().getName() + ":" + Infrastructure.getInstance().getCode());
                    }
                    if (protocol != null && protocol.equals("LEAVE")) {
                        writer.println(protocol + ":" + Infrastructure.getInstance().getCode() + ":" + Infrastructure.getInstance().getName());
                    }
                    if (protocol != null && protocol.equals("UPDATE")) {
                        writer.println(protocol + ":" + Infrastructure.getInstance().getCode() + ":" + Infrastructure.getInstance().getData());
                    }
                    if (protocol != null && protocol.equals("SEND_MESSAGE")) {
                        writer.println(protocol + ":" + Infrastructure.getInstance().getCode() + ":" + Infrastructure.getInstance().getName() + ":" + Infrastructure.getInstance().getOutgoingMessage());
                    }

                    Infrastructure.getInstance().setProtocol(null);
                }

                try {
                    socket.close();
                    System.out.println("Socket2 closed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}

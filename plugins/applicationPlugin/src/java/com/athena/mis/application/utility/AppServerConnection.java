/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/**
 * This program will demonstrate the file transfer from remote to local
 * $ CLASSPATH=.:../build javac ScpFrom.java
 * $ CLASSPATH=.:../build java ScpFrom user@remotehost:file1 file2
 * You will be asked passwd.
 * If everything works fine, a file 'file1' on 'remotehost' will copied to
 * local 'file1'.
 */
package com.athena.mis.application.utility;

import com.athena.mis.application.entity.AppServerInstance;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.*;
import java.util.HashMap;

public class AppServerConnection {


    private static Session initSession(String user, String host, int port, String pwd) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(pwd);
            return session;
        } catch (Exception e) {
            return null;
        }
    }

    public static String execute(AppServerInstance server, String command) {
        try {
            Session session = initSession(server.getSshUserName(), server.getSshHost(), server.getSshPort(), server.getSshPassword());
            if (session == null) return null;
            session.connect();

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();

            channel.connect();

            StringBuffer output = new StringBuffer();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    output.append(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    //System.out.println("exit-status: "+channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
            session.disconnect();
            return output.toString();

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static HashMap testConnection(AppServerInstance server, String command) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        try {
            Session session = initSession(server.getSshUserName(), server.getSshHost(), server.getSshPort(), server.getSshPassword());
            if (session == null) return null;
            session.connect();
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();

            channel.connect();
            channel.disconnect();
            session.disconnect();
            result.put("isError", Boolean.FALSE);
            return result;

        } catch (Exception e) {
            System.out.println(e);
            result.put("isError", Boolean.TRUE);
            result.put("exception", e.getMessage());
            return result;
        }
    }

    public static void copy(AppServerInstance server, String targetFile, String workDir) {

        FileOutputStream fos = null;
        try {
            Session session = initSession(server.getSshUserName(), server.getSshHost(), server.getSshPort(), server.getSshPassword());
            if (session == null) return;
            session.connect();

            String rfile = targetFile;
            String lfile = workDir;
            String prefix = null;
            if (new File(lfile).isDirectory()) {
                prefix = lfile + File.separator;
            }

            // exec 'scp -f rfile' remotely
            String command = "scp -f " + rfile;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            byte[] buf = new byte[1024];

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            while (true) {
                int c = checkAck(in);
                if (c != 'C') {
                    break;
                }

                // read '0644 '
                in.read(buf, 0, 5);

                long filesize = 0L;
                while (true) {
                    if (in.read(buf, 0, 1) < 0) {
                        // error
                        break;
                    }
                    if (buf[0] == ' ') break;
                    filesize = filesize * 10L + (long) (buf[0] - '0');
                }

                String file = null;
                for (int i = 0; ; i++) {
                    in.read(buf, i, 1);
                    if (buf[i] == (byte) 0x0a) {
                        file = new String(buf, 0, i);
                        break;
                    }
                }

                //System.out.println("filesize="+filesize+", file="+file);

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();

                // read a content of lfile
                fos = new FileOutputStream(prefix == null ? lfile : prefix + file);
                int foo;
                while (true) {
                    if (buf.length < filesize) foo = buf.length;
                    else foo = (int) filesize;
                    foo = in.read(buf, 0, foo);
                    if (foo < 0) {
                        // error
                        break;
                    }
                    fos.write(buf, 0, foo);
                    filesize -= foo;
                    if (filesize == 0L) break;
                }
                fos.close();
                fos = null;

                if (checkAck(in) != 0) {
                    //System.exit(0);
                    return;
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
                System.out.println("File transfer complete...");
            }

            session.disconnect();
            return;
            //System.exit(0);
        } catch (Exception e) {
            System.out.println(e);
            try {
                if (fos != null) fos.close();
            } catch (Exception ee) {
            }
        }
    }

    static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

}
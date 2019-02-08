package com.eu;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Test JSCH library with EUSEND SHS
 */
public class TestJsch {
    static final Logger LOG = LoggerFactory.getLogger(TestJsch.class);

    public static final String SHS_HOST = "";
    public static final int SHS_PORT = 22;
    public static final String SHS_USER = "";
    public static final String SHS_PASWD = "";
    public static final String DB_USER = "";
    public static final String DB_PASSWD = "";
    public static final String DB_HOST = "";
    public static final String DB_PORT = "";
    public static final String DB_SERVICE_NAME = "";


    public static void main(String[] args) {
        //testSSHConnection();
        testSSHConnection1();
    }

    public static void testSSHConnection() {
        JSch jsch = new JSch();
        Session session = null;
        Channel shellChannel = null;
        Channel execChannel = null;


        try {

            session = jsch.getSession(SHS_USER, SHS_HOST, SHS_PORT);
            session.setPassword(SHS_PASWD);
            // It must not be recommended, but if you want to skip host-key check, invoke following,
            session.setConfig("StrictHostKeyChecking", "no");

            //session.connect();
            session.connect(30000);   // making a connection with timeout.

            /*shellChannel = session.openChannel("shell");
            shellChannel.setInputStream(System.in);
            // a hack for MS-DOS prompt on Windows.
            shellChannel.setInputStream(new FilterInputStream(System.in) {
                public int read(byte[] b, int off, int len) throws IOException {
                    return in.read(b, off, (len > 1024 ? 1024 : len));
                }
            });
            shellChannel.setOutputStream(System.out);*/
            // Choose the pty-type "vt102".
            //((ChannelShell) channel).setPtyType("vt102");

            // Set environment variable "LANG" as "ja_JP.eucJP".
            //((ChannelShell) channel).setEnv("LANG", "ja_JP.eucJP");
            //shellChannel.connect(3*1000);


            execChannel = session.openChannel("exec");
            ((ChannelExec) execChannel).setCommand("ls -al");
            //channel.setInputStream(System.in);
            execChannel.setInputStream(null);
            execChannel.setOutputStream(System.out);
            ((ChannelExec) execChannel).setErrStream(System.err);

            InputStream in = execChannel.getInputStream();

            //channel.connect();
            execChannel.connect(3 * 1000);

            int k = 0;
            byte[] tmp = new byte[1024];
            //while(true){
            while (k < 10) {
                k++;
                LOG.info("k:" + k);

                if (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    LOG.info(new String(tmp, 0, i));
                }
                if (execChannel.isClosed()) {
                    if (in.available() > 0) continue;
                    LOG.info("exit-status: " + execChannel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }

            }


        } catch (Exception e) {
            LOG.error("Exception", e);
            e.printStackTrace();
        } finally {
            /*if (shellChannel != null && !shellChannel.isClosed()) {
                shellChannel.disconnect();
            }*/
            if (execChannel != null && !execChannel.isClosed()) {
                execChannel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public static void testSSHConnection1() {
        JSch jsch = new JSch();
        Session session = null;
        ChannelShell shellChannel = null;
        //ChannelExec execChannel = null;

        try {
            session = jsch.getSession(SHS_USER, SHS_HOST, SHS_PORT);
            session.setPassword(SHS_PASWD);
            // It must not be recommended, but if you want to skip host-key check, invoke following,
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");


            //session.connect();
            session.connect(30000);   // making a connection with timeout.

            shellChannel = (ChannelShell) session.openChannel("shell");
            //shellChannel.setInputStream(null);//(System.in);
            //shellChannel.setOutputStream(null);//(System.out);

            // Choose the pty-type "vt102".
            //((ChannelShell) shellChannel).setPtyType("vt102");

            // Set environment variable "LANG" as "ja_JP.eucJP".
            //((ChannelShell) shellChannel).setEnv("LANG", "ja_JP.eucJP");

            // a hack for MS-DOS prompt on Windows.
            /*shellChannel.setInputStream(new FilterInputStream(System.in) {
                public int read(byte[] b, int off, int len) throws IOException {
                    return in.read(b, off, (len > 1024 ? 1024 : len));
                }
            });*/

            shellChannel.connect(30000);


            InputStream inStream = shellChannel.getInputStream();
            OutputStream outStream = shellChannel.getOutputStream();
            String command0 = "stty -echo\n";
            String command1 = "source /ec/local/home/" + SHS_USER + "/.profile\n";
            String command2 = "which sqlplus\n";
            String command3 = "export DBUSER=\"" + DB_USER + "\"\n";
            String command4 = "export DBPASSWD=\"" + DB_PASSWD + "\"\n";
            String command5 = "export DBHOST=\"" + DB_HOST + "\"\n";
            String command6 = "export DBPORT=\"" + DB_PORT + "\"\n";
            String command7 = "export DBSERVICENAME=\"" + DB_SERVICE_NAME + "\"\n";
            //String command8 = "export CONNECTCMD=\"connect ${DBUSER}/${DBPASSWD}@\\\"(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=${DBHOST})(PORT=${DBPORT})))(CONNECT_DATA=(SERVICE_NAME=${DBSERVICENAME})))\\\"\"\n";
            String command8 = "export CONNECTCMD=\"connect ${DBUSER}/${DBPASSWD}@\\\"(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=${DBHOST})(PORT=${DBPORT})))(CONNECT_DATA=(SERVICE_NAME=${DBSERVICENAME})))\\\"\"\n";
            String command9 = "echo ${CONNECTCMD}\n";

            String command10 = "sqlplus -L -M \"CSV ON\" -S ${DBUSER}/${DBPASSWD}@\"(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=${DBHOST})(PORT=${DBPORT})))(CONNECT_DATA=(SERVICE_NAME=${DBSERVICENAME})))\"<<EOF\n" +
                    "set linesize 200;\n" +
                    "set pagesize 60;\n" +
                    "select * from etx_adt_config;\n" +
                    "quit;\n" +
                    "EOF\n";

            //outStream.write(command0.getBytes());
            //outStream.flush();
            //printToConsole(inStream);

            //LOG.info("Executing command " + command1);
            outStream.write(command1.getBytes());
            outStream.flush();
            //printToConsole(inStream);

            //LOG.info("Executing command " + command2);
            outStream.write(command2.getBytes());
            outStream.flush();
            //printToConsole(inStream);

            //LOG.info("Executing command " + command3);
            outStream.write(command3.getBytes());
            outStream.flush();
            //printToConsole(inStream);

            outStream.write(command4.getBytes());
            outStream.flush();
            //printToConsole(inStream);

            outStream.write(command5.getBytes());
            outStream.flush();
            //printToConsole(inStream);

            outStream.write(command6.getBytes());
            outStream.flush();
            //printToConsole(inStream);

            outStream.write(command7.getBytes());
            outStream.flush();
            //printToConsole(inStream);

            outStream.write(command8.getBytes());
            outStream.flush();
            //printToConsole(inStream);

            outStream.write(command9.getBytes());
            outStream.flush();
            printToConsole(inStream);

            outStream.write(command10.getBytes());
            outStream.flush();
            printToConsole(inStream);

            inStream.close();
            outStream.close();

        } catch (Exception e) {
            LOG.error("Exception", e);
            e.printStackTrace();
        } finally {


            LOG.info("In finally block");
            if (shellChannel != null && !shellChannel.isClosed()) {
                shellChannel.disconnect();
            }
            /*if (execChannel != null && !execChannel.isClosed()) {
                execChannel.disconnect();
            }*/
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }

    }

    public static void printToConsole(InputStream inStream) throws IOException {
        int k = 0;
        byte[] tmp = new byte[1024];
        //while(true){
        while (k < 10) {
            k++;
            LOG.info("k:" + k);

            if (inStream.available() > 0) {
                int i = inStream.read(tmp, 0, 1024);
                LOG.info("i=" + i);
                LOG.info("tmp=" + tmp);
                if (i < 0) break;
                LOG.info(new String(tmp, 0, i));
            }

            //LOG.info("instream.read at end:"+inStream.read());
            /*if (shellChannel.isClosed()) {
                if (inStream.available() > 0) continue;
                LOG.info("exit-status: " + shellChannel.getExitStatus());
                break;
            }*/
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }

        }
    }
}

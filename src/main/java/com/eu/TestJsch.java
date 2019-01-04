package com.eu;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FilterInputStream;
import java.io.IOException;

/**
 * Test JSCH library with EUSEND SHS
 */
public class TestJsch {
    static final Logger LOG = LoggerFactory.getLogger(TestJsch.class);

    public static final String SHS_HOST = "";
    public static final int    SHS_PORT = 22;
    public static final String SHS_USER = "";
    public static final String SHS_PASWD = "";

    public static void main(String[] args) {
//        LOG.info("Hello World!");
        testSSHConnection();
    }

    public static void testSSHConnection() {
        JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;

        try {

            session = jsch.getSession(SHS_USER, SHS_HOST, SHS_PORT);
            session.setPassword(SHS_PASWD);
            // It is not recommended, but if you want to skip host-key check, invoke following:
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000);   // making a connection with timeout.

            String command = "bash\n"+
                    "sqlplus -s /nolog <<EOF\n" +
                    "connect user/password@'connection_string'\n" +
                    "select 1 from dual;\n" +
                    "quit\n" +
                    "EOF\n";
            channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);

            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);
            ((ChannelExec) channel).setErrStream(System.err);

            // Choose the pty-type "vt102".
            //((ChannelShell)channel).setPtyType("vt102");

            /*
            // Set environment variable "LANG" as "ja_JP.eucJP".
            ((ChannelShell)channel).setEnv("LANG", "ja_JP.eucJP");
            */

            //channel.connect();
            channel.connect(3 * 1000);



        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        finally {
            if(channel != null && !channel.isClosed()){
                channel.disconnect();
            }
            if(session != null && session.isConnected()){
                session.disconnect();
            }
        }
    }
}

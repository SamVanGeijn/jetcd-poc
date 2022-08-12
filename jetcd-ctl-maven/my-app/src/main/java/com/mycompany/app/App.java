package com.mycompany.app;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import io.etcd.jetcd.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Draai project via de "play" knop in IntelliJ.
 *
 */
public class App 
{
    public static void main(String[] args) {
        Args global = new Args();
        CommandGet getCmd = new CommandGet();
        CommandPut putCmd = new CommandPut();
        CommandWatch watchCmd = new CommandWatch();

        JCommander jc = JCommander.newBuilder().addObject(global).addCommand("get", getCmd).addCommand("put", putCmd)
                .addCommand("watch", watchCmd).build();

        jc.parse(args);

        String cmd = jc.getParsedCommand();

        /* Note: in het originele project kon je via de command line commando's uitvoeren.
            Vanwege de "\0" key heb ik het direct in de code gezet. **/
        try (Client client = Client.builder().endpoints(global.endpoints.split(",")).build()) {
//            putCmd.accept(client);
            getCmd.accept(client); // Haal alle keys op.
            watchCmd.accept(client); // Watch alle keys.
        } catch (Exception e) {
            System.out.println(cmd + " Error {}");
            System.exit(1);
        }
    }

    public static class Args {
        @Parameter(names = { "--endpoints" }, description = "gRPC endpoints ")
        private String endpoints = "http://127.0.0.1:2379";

        @Parameter(names = { "-h", "--help" }, help = true)
        private boolean help = false;
    }
}

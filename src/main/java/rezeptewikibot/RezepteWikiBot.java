package rezeptewikibot;

import Rwb.Commands.CommandException;
import Rwb.Commands.Login;
import Rwb.Commands.WikiCommand;
import Rwb.Parser.CatBot;
import Rwb.Parser.ParseException;
import Rwb.Parser.Provider;
import Rwb.Parser.StreamProvider;
import Rwb.Wiki;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 *
 * @author Niki Hansche
 */
public class RezepteWikiBot extends Wiki {

    public RezepteWikiBot() {
        super("www.kochwiki.org", "/w");
        //setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 3128)));
        setThrottle(1000);
        setUsingCompressedRequests(false);
        setMarkBot(true);
        setMarkMinor(true);
    }

    public static RezepteWikiBot buildAndLogin() throws SecurityException {
        RezepteWikiBot rw = null;
        try {
            File f = new File("logindat.rwb");
            if (!f.exists()) {
                Logger.getLogger("wiki").log(Level.SEVERE, "Bitte erst \"login\" benutzen.");
                System.exit(1);
            } else {
                try {
                    try (FileInputStream fis = new FileInputStream(f)){
                        rw = (RezepteWikiBot) new ObjectInputStream(fis).readObject();
                    }
                } catch (InvalidClassException ex) {
                    Logger.getLogger("wiki").log(Level.SEVERE, "Bitte erneut \"login\" benutzen.");
                    System.exit(1);
                }

            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger("wiki").log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        return rw;
    }

    public static CommandLine handleCommandLine(String[] args) throws org.apache.commons.cli.ParseException {
        Options options = new Options();
        options.addOption("r", "run", true, "Run script");
        options.addOption("l", "login", true, "Login");
        options.addOption("h", "help", false, "Help");

        DefaultParser p = new DefaultParser();
        CommandLine commandline = p.parse(options, args);

        if (commandline.hasOption("h")) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("RezepteWikiBot", options);
            System.exit(0);
        }

        return commandline;
    }

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws Rwb.Parser.ParseException
     * @throws org.apache.commons.cli.ParseException
     */
    public static void main(String[] args) throws FileNotFoundException, ParseException, IOException, org.apache.commons.cli.ParseException {
        RezepteWikiBot rw;
        CommandLine cmdl = handleCommandLine(args);

        try {
            if (cmdl.hasOption("l")) {
                rw = new RezepteWikiBot();
                new Login(cmdl.getOptionValue("l")).execute(rw);
                rw.saveThis("logindat.rwb");
            }

            if (cmdl.hasOption("r")) {
                rw = buildAndLogin();
                rw.setLogLevel(Level.WARNING);
                runOrganize(rw, cmdl.getOptionValue("r"));
                rw.logout();
            }
        } catch (CommandException ex) {
            Logger.getLogger("wiki").log(Level.SEVERE, null, ex);
        }
    }

    public static void runOrganize(Wiki rw, String filename) throws IOException, ParseException, CommandException, FileNotFoundException {
        Provider p = new StreamProvider(new BufferedReader(new FileReader(filename)));
        try {
            List<WikiCommand> lwc = new CatBot(p).Input();
            for (WikiCommand wikiCommand : lwc) {
                Logger.getLogger("wiki").log(Level.INFO, wikiCommand.toString());
                wikiCommand.execute(rw);
            }
        } finally {
            p.close();
        }
    }
}

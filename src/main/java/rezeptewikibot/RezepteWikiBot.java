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

/**
 *
 * @author Niki Hansche
 */
public class RezepteWikiBot extends Wiki {

    public RezepteWikiBot() {
        super("http", "www.kochwiki.org", "/w");
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
                Logger.getLogger(RezepteWikiBot.class.getName()).log(Level.SEVERE, "Bitte erst \"login\" benutzen.");
                System.exit(1);
            } else {
                try {
                    rw = (RezepteWikiBot) new ObjectInputStream(new FileInputStream(f)).readObject();
                } catch (InvalidClassException ex) {
                    Logger.getLogger(RezepteWikiBot.class.getName()).log(Level.SEVERE, "Bitte erneut \"login\" benutzen.");
                    System.exit(1);
                }

            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(RezepteWikiBot.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rw;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, ParseException, IOException {
        RezepteWikiBot rw;
        
        try {

            if (args.length < 1) {
                Logger.getLogger(RezepteWikiBot.class.getName()).log(Level.SEVERE, "Benutzung: java -jar RezepteWiki.jar login <Benutzername> | organize <Dateiname>");
                System.exit(1);

            }

            if ("login".equals(args[0])) {
                if (args.length<2) {
                    Logger.getLogger(RezepteWikiBot.class.getName()).log(Level.SEVERE, "Parameter: Benutzername erwartet.");
                    System.exit(1);
                }
                rw = new RezepteWikiBot();
                //rw.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 3128)));
                new Login(args[1]).execute(rw);
                rw.saveThis("logindat.rwb");
            } else {
                rw = buildAndLogin();
                //rw.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 3128)));
            }

            rw.setLoglevel(Level.WARNING);

            switch (args[0]) {
                case "organize":
                    if (args.length > 1) {
                        runOrganize(rw, args[1]);
                    } else {
                        Logger.getLogger(RezepteWikiBot.class.getName()).log(Level.SEVERE, "\"Organize\" benötigt einen Dateinamen als Parameter.");
                    }
                    break;
                case "run":
                    if (args.length > 1) {
                        runOrganize(rw, args[1]);
                    } else {
                        Logger.getLogger(RezepteWikiBot.class.getName()).log(Level.SEVERE, "\"Run\" benötigt einen Dateinamen als Parameter.");
                    }
                    break;
            }

            rw.logout();
        } catch (CommandException ex) {
            Logger.getLogger(RezepteWikiBot.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public static void runOrganize(Wiki rw, String filename) throws IOException, ParseException, CommandException, FileNotFoundException {
        Provider p = new StreamProvider(new BufferedReader(new FileReader(filename)));
        try {
            List<WikiCommand> lwc = new CatBot(p).Input();
            for (WikiCommand wikiCommand : lwc) {
                Logger.getLogger(RezepteWikiBot.class.getName()).log(Level.INFO, wikiCommand.toString());
                wikiCommand.execute(rw);
            }
        } finally {
            p.close();

        }
    }

}

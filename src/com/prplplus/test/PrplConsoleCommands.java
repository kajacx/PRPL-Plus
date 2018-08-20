package com.prplplus.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PrplConsoleCommands {
    public static boolean testMode = false;
    public static String language;

    public static String wikiUrl;
    public static String wikiBaseUrl = "https://knucklecracker.com";

    public static boolean useCache = false;
    public static String serializedPath;

    private static Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) throws Exception {
        //set to PF
        wikiUrl = "https://knucklecracker.com/wiki/doku.php?id=prpl:prplreference";
        serializedPath = "wiki_prpl_serialized.dat";
        language = "PRPL";

        //set to CW3
        //wikiUrl = "https://knucklecracker.com/wiki/doku.php?id=crpl:crplreference";
        //serializedPath = "wiki_crpl_serialized.dat";
        //language = "CRPL";


        List<ProcessedTable> tables = loadTables();

        System.out.println("CreateList ~>wikiTables");
        System.out.println();
        tables.forEach(PrplConsoleCommands::printTable);

        /*System.out.println("<?xml version=\"1.0\" encoding=\"Utf-8\" ?>\r\n" +
                "<NotepadPlus>\r\n" +
                "\t<AutoComplete language=\"" + language + "\">");
        for (ProcessedTable table : tables) {
            for (Command c : table.commands) {
                System.out.format("\t\t<KeyWord name=\"%s\" func=\"yes\">%n", c.name);
                System.out.format("\t\t\t<Overload retVal=\"%s\" descr=\"%s%n%s\">%n", escapeParam(c.output), escapeParam(c.notation), escapeParam(c.description));
                if (c.input != null && !c.input.trim().isEmpty()) {
                    System.out.format("\t\t\t\t<Param name=\"%s\" />%n", c.input);
                }
                System.out.format("\t\t\t</Overload>%n");
                System.out.format("\t\t</KeyWord>%n");
                //System.out.format("#%s%n", c.name, c.input.replace(" ", ""));
                //System.out.format("#%s-%s-%s-%s%n", c.name, c.input.replace(" ", "_"), c.output.replace(" ", "_"), c.notation.replace(" ", "_"));
                //System.out.format("#%s%s%n", c.name, c.notation.replace(" ", "_").replace("[", "-").replace("]", ""));
            }
        }
        System.out.println("\t</AutoComplete>\r\n" +
                "</NotepadPlus>");*/
    }

    public static String escapeParam(String param) {
        if (param == null) {
            return "";
        }
        return param.replace("&", "&amp;").replace("\"", "&quot;");
    }

    public static void printTable(ProcessedTable table) {
        String tableName = table.tableName.replaceAll("[/ ]", "");
        System.out.format("<~wikiTables \"%s\" AppendToList%n", tableName);
        System.out.format("CreateList ~>wikiTable%s%n", tableName);
        System.out.format("%n");
        for (Command command : table.commands) {
            System.out.format("    CreateList%n");
            System.out.format("        dup \"%s\" AppendToList%n", command.name);
            System.out.format("        dup \"%s\" AppendToList%n", command.input);
            System.out.format("        dup \"%s\" AppendToList%n", command.output);
            System.out.format("        dup \"%s\" AppendToList%n", command.notation);
            System.out.format("        dup %s AppendToList%n", fullyEncodePrplString(command.description));
            System.out.format("        dup %s AppendToList%n", fullyEncodePrplString(command.examples));
            System.out.format("    <~wikiTable%s swap AppendToList %n", tableName);
            System.out.format("    %n");
        }
    }

    public static String fullyEncodePrplString(String string) {
        StringBuilder builder = new StringBuilder();

        int i = 0;
        String[] lines = string.split("\r?\n");
        for (String line : lines) {

            if (i > 0) {
                builder.append(" LF Concat ");
            }

            int j = 0;
            String[] words = line.split("\"");
            for (String word : words) {
                if (j > 0) {
                    builder.append(" DoubleQuote Concat ");
                }
                builder.append('"').append(word).append('"');
                if (j > 0) {
                    builder.append(" Concat");
                }
                j++;
            }

            if (i > 0) {
                builder.append(" Concat");
            }

            i++;
        }

        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    public static List<ProcessedTable> loadTables() throws Exception {
        List<ProcessedTable> tables = null;

        if (useCache) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializedPath));
                tables = (List<ProcessedTable>) ois.readObject();
                ois.close();
            } catch (IOException ex) {
                ex.printStackTrace(System.out);
            }
        }

        if (tables == null) {

            URL url = new URL(wikiUrl);
            //InputStream is = url.openStream(); // throws an IOException

            Document doc = Jsoup.parse(url, 2000);

            //Elements els = doc.select("table tr td a");
            //System.out.println(els);

            Elements tableEls = doc.select("table.inline");
            tables = new ArrayList<>();
            for (Element table : tableEls) {
                tables.add(processTable(table));
            }

            if (!testMode) {
                testMax = queue.size();
            }

            Thread[] workers = new Thread[24];
            for (int i = 0; i < workers.length; i++) {
                workers[i] = new Thread(() -> {
                    Runnable job;
                    while ((job = queue.poll()) != null) {
                        job.run();
                    }
                });
            }

            for (int i = 0; i < workers.length; i++) {
                workers[i].start();
            }

            for (int i = 0; i < workers.length; i++) {
                workers[i].join();
            }

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serializedPath));
            oos.writeObject(tables);
            oos.close();
        }

        return tables;
    }

    public static ProcessedTable processTable(Element table) {
        ProcessedTable result = new ProcessedTable();

        result.tableName = table.select("thead tr th").text();

        for (Element link : table.select("tbody tr td a")) {
            final Command command = new Command();
            final String href = link.attr("href");
            result.commands.add(command);
            queue.add(() -> fillCommand(href, command));
        }

        return result;
    }

    private static int testMax = 12;
    private static AtomicInteger testCur = new AtomicInteger();

    public static void fillCommand(final String link, final Command command) {
        if (!testMode || testCur.get() < testMax) {
            System.out.format("Parsing %d out of %d%n", testCur.incrementAndGet(), testMax);
            try {
                URL url = new URL(wikiBaseUrl + link);
                Document doc = Jsoup.parse(url, 2000);

                command.name = doc.select("h2.sectionedit1").text();

                command.input = doc.select("td.col0").text();
                command.output = doc.select("td.col1").text();
                command.notation = doc.select("td.col2").text();

                command.description = doc.select("h4#description+div").text();
                command.examples = doc.select("h4#examples+div").text().replace("\t", "    "); //because f**k tabs, that's why

            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        } else {
            command.name = link;
        }
    }

    static class ProcessedTable implements Serializable {
        public String tableName;
        public List<Command> commands = new ArrayList<>(); //concurrent, because values will be inserted from multiple threads

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ProcessedTable [");
            if (tableName != null) {
                builder.append("tableName=");
                builder.append(tableName);
                builder.append(", ");
            }
            if (commands != null) {
                builder.append("commands=");
                builder.append(commands);
            }
            builder.append("]");
            return builder.toString();
        }

    }

    static class Command implements Serializable {
        public String name;
        public String input;
        public String output;
        public String notation;
        public String description;
        public String examples;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Command [");
            if (name != null) {
                builder.append("name=");
                builder.append(name);
                builder.append(", ");
            }
            if (input != null) {
                builder.append("input=");
                builder.append(input);
                builder.append(", ");
            }
            if (output != null) {
                builder.append("output=");
                builder.append(output);
                builder.append(", ");
            }
            if (notation != null) {
                builder.append("notation=");
                builder.append(notation);
                builder.append(", ");
            }
            if (description != null) {
                builder.append("description=");
                builder.append(description);
                builder.append(", ");
            }
            if (examples != null) {
                builder.append("examples=");
                builder.append(examples);
            }
            builder.append("]");
            return builder.toString();
        }
    }
}

/*String allCommands = ".field public static literal valuetype CrplCore/STATEMENT PUSH = int32(0x00000000)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT READ = int32(0x00000001)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT WRITE = int32(0x00000002)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DELETE = int32(0x00000003)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT REFREAD = int32(0x00000004)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT REFWRITE = int32(0x00000005)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT REFEXISTS = int32(0x00000006)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT REFDELETE = int32(0x00000007)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT EXISTS = int32(0x00000008)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CLEARLOCALS = int32(0x00000009)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT READGLOBAL = int32(0x0000000A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT WRITEGLOBAL = int32(0x0000000B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DELETEGLOBAL = int32(0x0000000C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT REFREADGLOBAL = int32(0x0000000D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT REFWRITEGLOBAL = int32(0x0000000E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT REFEXISTSGLOBAL = int32(0x0000000F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT REFDELETEGLOBAL = int32(0x00000010)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT EXISTSGLOBAL = int32(0x00000011)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CLEARGLOBALS = int32(0x00000012)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT OPENTRAN = int32(0x00000013)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CLOSETRAN = int32(0x00000014)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT OPENBRACKET = int32(0x00000015)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CLOSEBRACKET = int32(0x00000016)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT NOTPERSIST = int32(0x00000017)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT IF = int32(0x00000018)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ELSE = int32(0x00000019)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ENDIF = int32(0x0000001A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DO = int32(0x0000001B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT LOOP = int32(0x0000001C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT I = int32(0x0000001D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT J = int32(0x0000001E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT K = int32(0x0000001F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT WHILE = int32(0x00000020)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT REPEAT = int32(0x00000021)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ENDWHILE = int32(0x00000022)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT BREAK = int32(0x00000023)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ONCE = int32(0x00000024)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ENDONCE = int32(0x00000025)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT INVOCATIONCOUNT = int32(0x00000026)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DELAY = int32(0x00000027)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT RANDINT = int32(0x00000028)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT RANDFLOAT = int32(0x00000029)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DUP = int32(0x0000002A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DUP2 = int32(0x0000002B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SWAP = int32(0x0000002C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT POP = int32(0x0000002D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CLEARSTACK = int32(0x0000002E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT STACKSIZE = int32(0x0000002F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ASINT = int32(0x00000030)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ASFLOAT = int32(0x00000031)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GT = int32(0x00000032)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GTE = int32(0x00000033)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT LT = int32(0x00000034)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT LTE = int32(0x00000035)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT EQ = int32(0x00000036)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT NEQ = int32(0x00000037)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT EQ0 = int32(0x00000038)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT NEQ0 = int32(0x00000039)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ADD = int32(0x0000003A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SUB = int32(0x0000003B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT MUL = int32(0x0000003C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DIV = int32(0x0000003D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT MOD = int32(0x0000003E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ABS = int32(0x0000003F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ROUND = int32(0x00000040)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT NEG = int32(0x00000041)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SIN = int32(0x00000042)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT COS = int32(0x00000043)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TAN = int32(0x00000044)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ASIN = int32(0x00000045)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ACOS = int32(0x00000046)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ATAN = int32(0x00000047)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ATAN2 = int32(0x00000048)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PI = int32(0x00000049)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TAU = int32(0x0000004A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TWOPI = int32(0x0000004B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT HALFPI = int32(0x0000004C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT QUARTERPI = int32(0x0000004D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT E = int32(0x0000004E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT RAD2DEG = int32(0x0000004F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DEG2RAD = int32(0x00000050)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SIGNALGENERATOR = int32(0x00000051)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT POW = int32(0x00000052)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SQRT = int32(0x00000053)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT LOG = int32(0x00000054)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT LN = int32(0x00000055)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT LOG10 = int32(0x00000056)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT MIN = int32(0x00000057)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT MAX = int32(0x00000058)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT FLOOR = int32(0x00000059)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CEIL = int32(0x0000005A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT APPROXIMATELY = int32(0x0000005B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT AVG2 = int32(0x0000005C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT AND = int32(0x0000005D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT OR = int32(0x0000005E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT XOR = int32(0x0000005F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT NOT = int32(0x00000060)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TRUE = int32(0x00000061)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT FALSE = int32(0x00000062)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DOUBLEQUOTE = int32(0x00000063)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CR = int32(0x00000064)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT LF = int32(0x00000065)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETTYPE = int32(0x00000066)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CREATELIST = int32(0x00000067)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CREATELISTSTARTINGSIZE = int32(0x00000068)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETLISTELEMENT = int32(0x00000069)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETLISTELEMENT = int32(0x0000006A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETLISTELEMENTRPN = int32(0x0000006B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT APPENDTOLIST = int32(0x0000006C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PREPENDTOLIST = int32(0x0000006D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT APPENDSTACKTOLIST = int32(0x0000006E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PREPENDSTACKTOLIST = int32(0x0000006F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT INSERTLISTELEMENT = int32(0x00000070)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT REMOVELISTELEMENT = int32(0x00000071)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETLISTCOUNT = int32(0x00000072)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT COPYLIST = int32(0x00000073)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DEEPCOPYLIST = int32(0x00000074)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETKEY = int32(0x00000075)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETKEYDOWN = int32(0x00000076)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETKEYUP = int32(0x00000077)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETMOUSEBUTTON = int32(0x00000078)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETMOUSEBUTTONDOWN = int32(0x00000079)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETMOUSEBUTTONUP = int32(0x0000007A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETMOUSEPOSITION = int32(0x0000007B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETMOUSESCREENPOSITION = int32(0x0000007C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETMOUSESCREENPIXELPOSITION = int32(0x0000007D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETMOUSECELL = int32(0x0000007E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT RESETGAMETIME = int32(0x0000007F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETGAMETIME = int32(0x00000080)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETGAMETIMEFRAMES = int32(0x00000081)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETGAMETIMEFRAMES = int32(0x00000082)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PAUSEGAME = int32(0x00000083)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT UNPAUSEGAME = int32(0x00000084)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT OPERATEWHILEPAUSED = int32(0x00000085)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ISPAUSED = int32(0x00000086)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT USERCANCELED = int32(0x00000087)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DISTANCE = int32(0x00000088)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SHORTESTANGLE = int32(0x00000089)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONCAT = int32(0x0000008A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SUBSTRING = int32(0x0000008B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT STARTSWITH = int32(0x0000008C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ENDSWITH = int32(0x0000008D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SPLIT = int32(0x0000008E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT STRINGTOLIST = int32(0x0000008F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT STRINGLENGTH = int32(0x00000090)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TOUPPER = int32(0x00000091)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TOLOWER = int32(0x00000092)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT STRINGREPLACE = int32(0x00000093)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT MAPWIDTH = int32(0x00000094)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT MAPHEIGHT = int32(0x00000095)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT MAPCELLWIDTH = int32(0x00000096)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT MAPCELLHEIGHT = int32(0x00000097)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SCREENWIDTH = int32(0x00000098)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SCREENHEIGHT = int32(0x00000099)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT RANDXCOORD = int32(0x0000009A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT RANDYCOORD = int32(0x0000009B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT RANDCOORDS = int32(0x0000009C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT RANDCOORDSINRANGE = int32(0x0000009D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CURRENTCOORDS = int32(0x0000009E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CURRENTX = int32(0x0000009F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CURRENTY = int32(0x000000A0)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITCURRENTCOORDS = int32(0x000000A1)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITCURRENTX = int32(0x000000A2)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITCURRENTY = int32(0x000000A3)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETCURRENTCOORDS = int32(0x000000A4)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETCURRENTX = int32(0x000000A5)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETCURRENTY = int32(0x000000A6)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CURRENTPIXELCOORDS = int32(0x000000A7)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITTRANSFORMPOSITION = int32(0x000000A8)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSCREENMODE = int32(0x000000A9)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSCREENCOORDS = int32(0x000000AA)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSCREENCOORDX = int32(0x000000AB)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSCREENCOORDY = int32(0x000000AC)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSCREENPIXELCOORDS = int32(0x000000AD)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSCREENPIXELCOORDX = int32(0x000000AE)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSCREENPIXELCOORDY = int32(0x000000AF)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETBUILDSLOTSCREENCOORDS = int32(0x000000B0)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETCONTROLPANELBUTTONSCREENCOORDS = int32(0x000000B1)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETTECHPANELVISIBLE = int32(0x000000B2)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTECHPANELVISIBLE = int32(0x000000B3)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITSELECTABLE = int32(0x000000B4)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CELLTOPIXEL = int32(0x000000B5)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PIXELTOCELL = int32(0x000000B6)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CREATEEFFECT = int32(0x000000B7)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DESTROYEFFECT = int32(0x000000B8)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETALLUNITSINRANGE = int32(0x000000B9)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETALLSHIPSINRANGE = int32(0x000000BA)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETNEARESTSHIPINRANGE = int32(0x000000BB)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETCORESWITHVAR = int32(0x000000BC)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ADDSCRIPTTOUNIT = int32(0x000000BD)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSCRIPTVAR = int32(0x000000BE)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSCRIPTVAR = int32(0x000000BF)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETIMAGE = int32(0x000000C0)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT REMOVEIMAGES = int32(0x000000C1)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETIMAGECOLOR = int32(0x000000C2)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETIMAGEROTATION = int32(0x000000C3)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETIMAGEPOSITION = int32(0x000000C4)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETIMAGEPOSITIONX = int32(0x000000C5)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETIMAGEPOSITIONY = int32(0x000000C6)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETIMAGEPOSITIONZ = int32(0x000000C7)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETIMAGELAYER = int32(0x000000C8)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETIMAGEORDER = int32(0x000000C9)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETIMAGESCALE = int32(0x000000CA)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETIMAGESCALEX = int32(0x000000CB)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETIMAGESCALEY = int32(0x000000CC)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETIMAGECOLOR = int32(0x000000CD)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETIMAGEROTATION = int32(0x000000CE)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETIMAGEPOSITION = int32(0x000000CF)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETIMAGEPOSITIONX = int32(0x000000D0)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETIMAGEPOSITIONY = int32(0x000000D1)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETIMAGEPOSITIONZ = int32(0x000000D2)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETIMAGELAYER = int32(0x000000D3)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETIMAGEORDER = int32(0x000000D4)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETIMAGESCALE = int32(0x000000D5)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETIMAGESCALEX = int32(0x000000D6)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETIMAGESCALEY = int32(0x000000D7)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT WINMISSION = int32(0x000000D8)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT FAILMISSION = int32(0x000000D9)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETFAILONHQLOSS = int32(0x000000DA)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETFAILONHQLOSS = int32(0x000000DB)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETHQJUMPTIME = int32(0x000000DC)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHOWSPECIALENDING = int32(0x000000DD)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PLAYSOUND = int32(0x000000DE)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PLAYSOUNDWITHVOLUME = int32(0x000000DF)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTEXT = int32(0x000000E0)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTEXTCOLOR = int32(0x000000E1)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTEXTSIZE = int32(0x000000E2)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTEXTX = int32(0x000000E3)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTEXTY = int32(0x000000E4)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTEXTROTATION = int32(0x000000E5)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTEXTORDER = int32(0x000000E6)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTEXTALIGNMENT = int32(0x000000E7)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTEXTPIVOTX = int32(0x000000E8)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTEXTPIVOTY = int32(0x000000E9)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SHOWCONVERSATION = int32(0x000000EA)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SHOWCONVERSATIONNOPAUSE = int32(0x000000EB)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SHOWCONVERSATIONAUTOMODE = int32(0x000000EC)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CLOSECONVERSATION = int32(0x000000ED)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ADDCONVERSATIONMESSAGE = int32(0x000000EE)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CLEARCONVERSATION = int32(0x000000EF)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DESTROYCONVERSATION = int32(0x000000F0)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SHOWMESSAGE = int32(0x000000F1)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SHOWMESSAGEDISMISSIBLE = int32(0x000000F2)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT WASMESSAGEDISMISSED = int32(0x000000F3)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONVERSATIONSHOWING = int32(0x000000F4)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SPECIFICCONVERSATIONSHOWING = int32(0x000000F5)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CREATEINDICATORARROW = int32(0x000000F6)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DESTROYINDICATORARROW = int32(0x000000F7)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETINDICATORARROWPOSITION = int32(0x000000F8)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETINDICATORARROWROTATION = int32(0x000000F9)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETINDICATORARROWCOLOR = int32(0x000000FA)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SELF = int32(0x000000FB)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPATTRIBUTE = int32(0x000000FC)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHIPATTRIBUTE = int32(0x000000FD)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETATTRIBUTE = int32(0x000000FE)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETATTRIBUTE = int32(0x000000FF)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITTYPE = int32(0x00000100)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTIMER0 = int32(0x00000101)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTIMER1 = int32(0x00000102)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTIMER2 = int32(0x00000103)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTIMER3 = int32(0x00000104)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETTIMER0 = int32(0x00000105)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETTIMER1 = int32(0x00000106)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETTIMER2 = int32(0x00000107)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETTIMER3 = int32(0x00000108)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ISEDITMODE = int32(0x00000109)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DEBUG = int32(0x0000010A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TRACE = int32(0x0000010B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TRACE2 = int32(0x0000010C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TRACE3 = int32(0x0000010D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TRACE4 = int32(0x0000010E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TRACE5 = int32(0x0000010F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TRACEALL = int32(0x00000110)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TRACEALLSP = int32(0x00000111)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TRACESTACK = int32(0x00000112)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CLEARTRACELOG = int32(0x00000113)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUPDATECOUNT = int32(0x00000114)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PRINT = int32(0x00000115)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PRINT2 = int32(0x00000116)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PRINT3 = int32(0x00000117)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PRINT4 = int32(0x00000118)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PRINT5 = int32(0x00000119)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PRINTALL = int32(0x0000011A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PRINTALLSP = int32(0x0000011B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PRINTSTACK = int32(0x0000011C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CALL = int32(0x0000011D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT FUNC = int32(0x0000011E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT RETURN = int32(0x0000011F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT EOS = int32(0x00000120)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT NULLCOMMAND = int32(0x00000121)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_LATHETARGETS = int32(0x00000122)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_CREATEENERGYSOURCEWHENDESTROYED = int32(0x00000123)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_CREATEAMPGEMWHENDESTROYED = int32(0x00000124)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_ISENEMY = int32(0x00000125)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_GROWSTRUC = int32(0x00000126)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_CREATEFOOTPRINT = int32(0x00000127)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_MISSIONGOAL = int32(0x00000128)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_TAKEMAPSPACE = int32(0x00000129)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_ISBUILDING = int32(0x0000012A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_ISDESTROYED = int32(0x0000012B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_SHIP_ISENEMY = int32(0x0000012C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_SHIP_HULLSIZE = int32(0x0000012D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_SHIP_HULLBUILT = int32(0x0000012E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_SHIP_CMCOST = int32(0x0000012F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_SHIP_CMBUILTAMT = int32(0x00000130)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_SHIP_ISCONNECTEDTOENERGYMINE = int32(0x00000131)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_COORDX = int32(0x00000132)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_COORDY = int32(0x00000133)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_PIXELCOORDX = int32(0x00000134)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_PIXELCOORDY = int32(0x00000135)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CONST_RECEIVESPACKETS = int32(0x00000136)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SIGNAL_NONE = int32(0x00000137)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SIGNAL_SINE = int32(0x00000138)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SIGNAL_SQUARE = int32(0x00000139)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SIGNAL_TRIANGLE = int32(0x0000013A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SIGNAL_SAWTOOTH = int32(0x0000013B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SIGNAL_RANDOM = int32(0x0000013C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SIGNAL_CONSTANT = int32(0x0000013D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ISUNIT = int32(0x0000013E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITISGOAL = int32(0x0000013F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITISGOAL = int32(0x00000140)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CREATEUNIT = int32(0x00000141)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DESTROYUNIT = int32(0x00000142)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT UNITISDESTROYED = int32(0x00000143)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT INITCANNONSHOT = int32(0x00000144)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT INITMISSILE = int32(0x00000145)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT MOVESHIP = int32(0x00000146)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ROTATESHIPTOCELL = int32(0x00000147)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ROTATESHIPTOPIXEL = int32(0x00000148)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ROTATESHIP = int32(0x00000149)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ROTATESHIPDEGREES = int32(0x0000014A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPHULLSECTION = int32(0x0000014B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPHULLSECTIONBUILT = int32(0x0000014C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DELETESHIPFROMSLOT = int32(0x0000014D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPFROMSLOT = int32(0x0000014E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSLOTFROMSHIP = int32(0x0000014F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPNAMEFROMSLOT = int32(0x00000150)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPSLOTCOUNT = int32(0x00000151)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ADDSTOCKSHIPTOINVENTORY = int32(0x00000152)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ADDCUSTOMSHIPTOINVENTORY = int32(0x00000153)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ADDGAMEEVENT = int32(0x00000154)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ADDGAMEEVENTATCELL = int32(0x00000155)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ADDGAMEEVENTATPIXEL = int32(0x00000156)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETGAMEEVENTFADETIME = int32(0x00000157)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETGAMEEVENTMESSAGE = int32(0x00000158)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETGAMEEVENTTYPE = int32(0x00000159)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETLANDCOUNT = int32(0x0000015A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETENEMYMIRECOUNT = int32(0x0000015B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETMIRECOUNT = int32(0x0000015C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETENERGYSOURCECOUNT = int32(0x0000015D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETENERGYSOURCES = int32(0x0000015E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETRANDOMENERGYSOURCE = int32(0x0000015F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETGLOBALSTOREDENERGY = int32(0x00000160)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETGLOBALSTOREDENERGY = int32(0x00000161)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETAMPGEMCOUNT = int32(0x00000162)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETAMPGEMCOUNT = int32(0x00000163)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETMAXBLUEEMERGENT = int32(0x00000164)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETMAXREDEMERGENT = int32(0x00000165)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETBLUEEMERGENTRATE = int32(0x00000166)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETREDEMERGENTRATE = int32(0x00000167)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETMAXBLUEEMERGENT = int32(0x00000168)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETMAXREDEMERGENT = int32(0x00000169)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETBLUEEMERGENTRATE = int32(0x0000016A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETREDEMERGENTRATE = int32(0x0000016B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CREATEEMERGENT = int32(0x0000016C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETTECHAVAILABILITY = int32(0x0000016D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETALLOWSHIPAMP = int32(0x0000016E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETTECHAVAILABILITY = int32(0x0000016F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETALLOWSHIPAMP = int32(0x00000170)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETHULLBUILDRATEMOD = int32(0x00000171)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPICKUPTYPE = int32(0x00000172)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETDOPPELBEHAVIOR = int32(0x00000173)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETDOPPELHOMECELL = int32(0x00000174)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DESTROYPARTICLE = int32(0x00000175)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DESTROYBOND = int32(0x00000176)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CREATEPARTICLE = int32(0x00000177)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT PARTICLEEXISTS = int32(0x00000178)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETOWNEDPARTICLECOUNT = int32(0x00000179)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CREATEBOND = int32(0x0000017A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT BONDEXISTS = int32(0x0000017B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLESINRANGE = int32(0x0000017C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLESINRADIUS = int32(0x0000017D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLECOUNTINRADIUS = int32(0x0000017E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETNEARESTTARGET = int32(0x0000017F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT DAMAGEPARTICLE = int32(0x00000180)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ISEMERGENT = int32(0x00000181)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ISHULL = int32(0x00000182)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ISPARTICLE = int32(0x00000183)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ISOMNI = int32(0x00000184)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLEPOSITION = int32(0x00000185)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLEPOSITION = int32(0x00000186)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT TELEPORTPARTICLE = int32(0x00000187)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT FINDPARTICLETARGET = int32(0x00000188)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLEMAXSPEED = int32(0x00000189)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLEMAXAGE = int32(0x0000018A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLEMAXDISTANCE = int32(0x0000018B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLEHEALTH = int32(0x0000018C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLEDESTROYATEDGE = int32(0x0000018D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLEMASS = int32(0x0000018E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLEDRAG = int32(0x0000018F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLEIMMEDIATEFORCE = int32(0x00000190)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLETARGET = int32(0x00000191)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLETARGETATTRACTIONINTERVAL = int32(0x00000192)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLETARGETATTRACTIONFORCE = int32(0x00000193)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLESAMEINTERACTION = int32(0x00000194)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLEMOTION = int32(0x00000195)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLEHASTELEPORTED = int32(0x00000196)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLEMAXSPEED = int32(0x00000197)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLEMAXAGE = int32(0x00000198)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLEMAXDISTANCE = int32(0x00000199)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLEHEALTH = int32(0x0000019A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLEDESTROYATEDGE = int32(0x0000019B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLEMASS = int32(0x0000019C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLEDRAG = int32(0x0000019D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLEIMMEDIATEFORCE = int32(0x0000019E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLETARGET = int32(0x0000019F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLETARGETATTRACTIONINTERVAL = int32(0x000001A0)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLETARGETATTRACTIONFORCE = int32(0x000001A1)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLESAMEINTERACTION = int32(0x000001A2)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLEMOTION = int32(0x000001A3)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLEFORCEPARAMS = int32(0x000001A4)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLEFORCEPARAMS = int32(0x000001A5)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLEFORCEDIRECTIONRELATIVEPARTICLE = int32(0x000001A6)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLEFORCESPEEDLIMIT = int32(0x000001A7)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLEINTERACTDELAY = int32(0x000001A8)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPARTICLEINTERACTDELAY = int32(0x000001A9)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPARTICLERETURNWHENRECALLED = int32(0x000001AA)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITRECALLCHILDPARTICLES = int32(0x000001AB)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETBONDMOVEMENTPARAMS = int32(0x000001AC)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETBONDMOVEMENTPARAMS = int32(0x000001AD)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETBONDLENGTH = int32(0x000001AE)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETBONDLENGTH = int32(0x000001AF)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETALLCONNECTEDPARTICLES = int32(0x000001B0)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETALLCONNECTEDPARTICLECOUNT = int32(0x000001B1)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETALLCONNECTEDBONDS = int32(0x000001B2)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETALLCONNECTEDBONDCOUNT = int32(0x000001B3)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETFIELDCELL = int32(0x000001B4)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CLEARALLFIELDS = int32(0x000001B5)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT ISHQPRESENT = int32(0x000001B6)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETHQS = int32(0x000001B7)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETLAND = int32(0x000001B8)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETLAND = int32(0x000001B9)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETLANDBULK = int32(0x000001BA)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETMIRE = int32(0x000001BB)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETMIRE = int32(0x000001BC)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT MIRELAND = int32(0x000001BD)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT MIRELAND2 = int32(0x000001BE)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETBLUEPLASMADECAYMAX = int32(0x000001BF)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETREDPLASMADECAYMAX = int32(0x000001C0)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETBLUEPLASMADECAYMAX = int32(0x000001C1)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETREDPLASMADECAYMAX = int32(0x000001C2)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPLASMA = int32(0x000001C3)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETPLASMADECAY = int32(0x000001C4)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPLASMA = int32(0x000001C5)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETPLASMABULK = int32(0x000001C6)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GROWSTRUC = int32(0x000001C7)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSTRUC = int32(0x000001C8)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSTRUCBUILT = int32(0x000001C9)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSTRUC = int32(0x000001CA)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSTRUCBULK = int32(0x000001CB)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERPARTICLETYPE = int32(0x000001CC)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERPRODUCTIONINTERVAL = int32(0x000001CD)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERMAXPARTICLES = int32(0x000001CE)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERMAXTIME = int32(0x000001CF)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERMAXDISTANCE = int32(0x000001D0)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERMAXPARTICLESPEED = int32(0x000001D1)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERPARTICLEHEALTH = int32(0x000001D2)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERTARGETPROBABILITY = int32(0x000001D3)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTEREMITTERTARGETPROBABILITY = int32(0x000001D4)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERRECALLRANGE = int32(0x000001D5)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERDESTROYEATEDGE = int32(0x000001D6)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERDESTROYATEDGE = int32(0x000001D7)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERENEMYPATTERN = int32(0x000001D8)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERBONDDISTANCE = int32(0x000001D9)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERBONDSTIFFNESS = int32(0x000001DA)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERMINCHAINLENGTH = int32(0x000001DB)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERMAXCHAINLENGTH = int32(0x000001DC)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETEMITTERSHAPESIZE = int32(0x000001DD)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERPARTICLETYPE = int32(0x000001DE)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERPRODUCTIONINTERVAL = int32(0x000001DF)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERMAXPARTICLES = int32(0x000001E0)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERMAXTIME = int32(0x000001E1)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERMAXDISTANCE = int32(0x000001E2)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERMAXPARTICLESPEED = int32(0x000001E3)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERPARTICLEHEALTH = int32(0x000001E4)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERTARGETPROBABILITY = int32(0x000001E5)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTEREMITTERTARGETPROBABILITY = int32(0x000001E6)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERRECALLRANGE = int32(0x000001E7)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERDESTROYEATEDGE = int32(0x000001E8)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERDESTROYATEDGE = int32(0x000001E9)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERENEMYPATTERN = int32(0x000001EA)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERBONDDISTANCE = int32(0x000001EB)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERBONDSTIFFNESS = int32(0x000001EC)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERMINCHAINLENGTH = int32(0x000001ED)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERMAXCHAINLENGTH = int32(0x000001EE)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETEMITTERSHAPESIZE = int32(0x000001EF)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETENERGYSOURCERATE = int32(0x000001F0)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETENERGYSOURCERANGE = int32(0x000001F1)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETENERGYSOURCEGROWENEMYENERGYMINE = int32(0x000001F2)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETENERGYSOURCERATE = int32(0x000001F3)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETENERGYSOURCERANGE = int32(0x000001F4)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETENERGYSOURCEGROWENEMYENERGYMINE = int32(0x000001F5)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITLATHETARGETS = int32(0x000001F6)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITCREATEENERGYSOURCEWHENDESTROYED = int32(0x000001F7)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITISENEMY = int32(0x000001F8)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITGROWSTRUC = int32(0x000001F9)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITCREATEAMPGEMWHENDESTROYED = int32(0x000001FA)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITCREATEFOOTPRINT = int32(0x000001FB)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITMISSIONGOAL = int32(0x000001FC)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITTAKEMAPSPACE = int32(0x000001FD)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITISBUILDING = int32(0x000001FE)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITISDESTROYED = int32(0x000001FF)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITCOORDS = int32(0x00000200)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITCOORDX = int32(0x00000201)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITCOORDY = int32(0x00000202)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITPIXELCOORDS = int32(0x00000203)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITPIXELCOORDX = int32(0x00000204)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITPIXELCOORDY = int32(0x00000205)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITRECEIVESPACKETS = int32(0x00000206)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITHEALTH = int32(0x00000207)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITMAXHEALTH = int32(0x00000208)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITENERGY = int32(0x00000209)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITMAXENERGY = int32(0x0000020A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITHASHEALTHBAR = int32(0x0000020B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITHASENERGYBAR = int32(0x0000020C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITENERGYPACKDELAY = int32(0x0000020D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITPARTICLESDAMAGE = int32(0x0000020E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITSHIPHULLDAMAGES = int32(0x0000020F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITMIREDAMAGES = int32(0x00000210)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITMIREDAMAGEAMT = int32(0x00000211)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETUNITLATHEDAMAGEAMT = int32(0x00000212)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITLATHETARGETS = int32(0x00000213)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITCREATEENERGYSOURCEWHENDESTROYED = int32(0x00000214)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITISENEMY = int32(0x00000215)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITGROWSTRUC = int32(0x00000216)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITCREATEAMPGEMWHENDESTROYED = int32(0x00000217)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITCREATEFOOTPRINT = int32(0x00000218)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITMISSIONGOAL = int32(0x00000219)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITTAKEMAPSPACE = int32(0x0000021A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITISBUILDING = int32(0x0000021B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITCOORDS = int32(0x0000021C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITCOORDX = int32(0x0000021D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITCOORDY = int32(0x0000021E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITPIXELCOORDS = int32(0x0000021F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITPIXELCOORDX = int32(0x00000220)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITPIXELCOORDY = int32(0x00000221)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITRECEIVESPACKETS = int32(0x00000222)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITHEALTH = int32(0x00000223)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITMAXHEALTH = int32(0x00000224)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITENERGY = int32(0x00000225)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITMAXENERGY = int32(0x00000226)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITHASHEALTHBAR = int32(0x00000227)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITHASENERGYBAR = int32(0x00000228)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITENERGYPACKDELAY = int32(0x00000229)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITPARTICLESDAMAGE = int32(0x0000022A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITSHIPHULLDAMAGES = int32(0x0000022B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITMIREDAMAGES = int32(0x0000022C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITMIREDAMAGEAMT = int32(0x0000022D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETUNITLATHEDAMAGEAMT = int32(0x0000022E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPISDESTROYED = int32(0x0000022F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPROTATION = int32(0x00000230)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPCOORDS = int32(0x00000231)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPCOORDX = int32(0x00000232)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPCOORDY = int32(0x00000233)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPPIXELCOORDS = int32(0x00000234)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPPIXELCOORDX = int32(0x00000235)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPPIXELCOORDY = int32(0x00000236)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPISENEMY = int32(0x00000237)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPHULLSIZE = int32(0x00000238)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPHULLBUILT = int32(0x00000239)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPCMCOST = int32(0x0000023A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPCMBUILTAMT = int32(0x0000023B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPISCONNECTEDTOENERGYMINE = int32(0x0000023C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHIPROTATION = int32(0x0000023D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHIPCOORDS = int32(0x0000023E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHIPCOORDX = int32(0x0000023F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHIPCOORDY = int32(0x00000240)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHIPPIXELCOORDS = int32(0x00000241)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHIPPIXELCOORDX = int32(0x00000242)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHIPPIXELCOORDY = int32(0x00000243)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHIPISENEMY = int32(0x00000244)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPROTATIONDEGREES = int32(0x00000245)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHIPROTATIONDEGREES = int32(0x00000246)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHIPTHRUSTMULTIPLIER = int32(0x00000247)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPTHRUSTMULTIPLIER = int32(0x00000248)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPHASAMP = int32(0x00000249)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPHULLWIDTH = int32(0x0000024A)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPHULLHEIGHT = int32(0x0000024B)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPENERGY = int32(0x0000024C)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHIPENERGY = int32(0x0000024D)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT GETSHIPENERGYCAP = int32(0x0000024E)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHIPENERGYCAP = int32(0x0000024F)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT SETSHIPMODULESAMMO = int32(0x00000250)\r\n" +
        "    .field public static literal valuetype CrplCore/STATEMENT CUSTOMMODCOMMAND = int32(0x00000251)";

for (String line : allCommands.split("\r\n")) {
    String name = line.trim().split(" ")[6];
    System.out.format("dup \"%1$s\" eq if%n" +
            "    pop %1$s 1 return%n" +
            "endif%n", name);
} //*/
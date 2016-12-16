package com.prplplus;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.prplplus.jflex.PrplPlusLexer;
import com.prplplus.scanner.CachedLexer;
import com.prplplus.scanner.PRPLCompiler;

public class PrplPlusTest {
    public static void main(String[] args) throws IOException {
        //File fileIn = new File("c:\\Users\\Karel\\Documents\\My Games\\particlefleet\\editor\\Creative Shipyard\\scripts\\test.prpl+");

        //System.setProperty("user.dir", "c:\\Users\\Karel\\Documents\\My Games\\particlefleet\\editor");

        String fineName = "Creative Shipyard\\scripts\\test.prpl+";

        /* File file = new File(fineName);
        System.out.println("Current Working Directory: " + file.getAbsolutePath());
        System.out.println(file.exists());
        System.out.println(fileIn.exists());
        
        System.out.println(file.getAbsolutePath().equals(fileIn.getAbsolutePath()));*/

        FileReader reader = new FileReader(new File(Settings.WORK_IN + fineName));
        PrintWriter writer = new PrintWriter(System.out);

        PrplPlusLexer lexer = new PrplPlusLexer(reader, fineName);
        CachedLexer cached = new CachedLexer(lexer);

        PRPLCompiler compiler = new PRPLCompiler(writer);

        compiler.compile(cached, true);

        reader.close();
        writer.flush();

        /*String lathe64 = ShipConstructor.constructLathe();
        
        Ship ship = ShipDeconstructor.deconstruct(
                "CgQAcm9vdAMBAHMGAFNoaXA5OekDAG1wc08AAAAAAIhBAADgQAAAIEEAAIhBAAAAQAAAYEEAAIhBAAAAQAAAwEAAAMBAAADgQAAA4EAAAMBAAADgQAAAcEEAAOBAAAAgQQAAiEEAAOBAAAAgQQAAoEAAAEBAAACAQAAAmEEAAEBAAAAQQQAAsEEAAEBAAACAQAAAgEAAAEBAAAAQQQAAgD8AAEBAAABQQQAAgEAAAEBAAABgQQAAAEEAAEBAAABAQQAAEEEAAEBAAABAQQAAYEEAAEBAAABgQQAAcEEAAOBAAADAQAAAAEAAAOBAAAAwQQAAAAAAAOBAAAAwQQAAsEEAAOBAAADAQAAAoEEAAIA/AABgQQAAgD8AAIA/AACIQQAAgD8AAIA/AACgQQAAgD8AAIA/AAC4QQAAgD8AAIA/AADQQQAAgD8AAIA/AABgQQAAsEEAAIA/AACIQQAAsEEAAIA/AACgQQAAsEEAAIA/AAC4QQAAsEEAAIA/AADQQQAAsEEAACBBAABwQQAAgEAAACBBAABwQQAAkEEAAIA/AADoQQAAsEEAAABAAACAQQAAuEEAAABAAACYQQAAuEEAAABAAACwQQAAuEEAAABAAADIQQAAuEEAAABAAADgQQAAuEEAAIA/AADoQQAAgD8AAABAAACAQQAAgD8AAABAAACYQQAAgD8AAABAAACwQQAAgD8AAABAAADgQQAAgD8AAABAAADIQQAAgD8AAABBAACoQQAAMEEAAEBBAACAQQAAMEEAAIBAAACAPwAAkEEAAIBAAACAPwAAYEEAAIBAAACAPwAAIEEAAIBAAACAPwAAwEAAAIBAAABAQAAAUEEAAIBAAABAQAAAMEEAAEBAAABQQQAAmEEAAEBAAACYQQAAAEEAAEBAAACYQQAAcEEAAABAAAD4QQAAqEEAAABAAAAAQgAAkEEAAABAAAAAQgAAmEEAAABAAAAAQgAAwEAAAABAAAAAQgAAoEAAAABAAAD4QQAAQEAAAABAAADwQQAAmEEAAABAAADwQQAAoEAAAABAAADwQQAAgEEAAABAAADwQQAAAEEAAABAAADgQQAAIEEAAABAAADgQQAAYEEAAIBAAADgQQAAgEEAAIBAAADQQQAAgEEAAIBAAADgQQAAAEEAAIBAAADQQQAAAEEAAIBAAADQQQAAIEEAAIBAAADQQQAAYEEAAABAAADQQQAAQEEAAKBAAAAAAAAAQEAAAKBAAAAAAAAA4EAAAKBAAAAAAAAAMEEAAKBAAAAAAAAAcEEAAKBAAAAAAAAAmEEBAgBodyIAAAABAgBoaBkAAAALAgBocFIDAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADgAAAAEAAAABAAAAAQAAAAsAAAALAAAACwAAAAsAAAALAAAACwAAAAsAAAALAAAACwAAAAsAAAALAAAACwAAAAsAAAALAAAACwAAAAsAAAALAAAADwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAARAAAADwAAAAAAAAAOAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAsAAAAAAAAAAAAAAA4AAAATAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAAAAAAAAQAAAAEAAAAAAAAAAQAAAAEAAAAAAAAAAQAAAAEAAAAAAAAAAQAAAAEAAAAAAAAAAQAAAAEAAAALAAAADwAAAAAAAAABAAAAAQAAAAAAAAARAAAADwAAAA4AAAABAAAAAQAAAAEAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAsAAAAAAAAAAQAAAAEAAAAAAAAAAAAAAAEAAAABAAAAAQAAAAEAAAABAAAAAAAAAAAAAAAAAAAADgAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAADwAAAAAAAAALAAAADwAAAAEAAAABAAAADwAAAAAAAAABAAAAAQAAAAwAAAAAAAAAAAAAAAAAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAAAAAAAAQAAAAsAAAAAAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAADwAAAAAAAAAOAAAAAQAAAAEAAAABAAAADAAAABEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAAMAAAAAAAAAAEAAAALAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACwAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAAMAAAAAAAAAAAAAAAOAAAAAQAAAAEAAAALAAAACwAAAAsAAAABAAAAAQAAAAsAAAALAAAACwAAAAsAAAALAAAAAQAAAAsAAAABAAAACwAAAAEAAAAPAAAAAAAAAAwAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAADwAAAA4AAAABAAAAAQAAAAEAAAABAAAADAAAAAAAAAANAAAAAQAAAAEAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOAAAACwAAAAsAAAABAAAACwAAAAEAAAAPAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAABAAAACwAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAwAAAAAAAAAAAAAAA4AAAABAAAAAQAAAAEAAAAPAAAAAAAAAAEAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAQAAAAsAAAALAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAALAAAACwAAAAsAAAALAAAAAQAAAAEAAAABAAAACwAAAAsAAAABAAAAAQAAAAEAAAALAAAACwAAAAEAAAATAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAEAAAALAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAADwAAAAAAAAAAAAAADQAAAAEAAAABAAAAAQAAAAwAAAAAAAAAAQAAAAEAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAADwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADQAAAAsAAAALAAAAAQAAAAsAAAABAAAADAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAADAAAAA0AAAABAAAAAQAAAAEAAAABAAAADwAAAAAAAAAOAAAAAQAAAAEAAAAPAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAA8AAAAAAAAAAAAAAA0AAAABAAAAAQAAAAsAAAALAAAACwAAAAEAAAABAAAACwAAAAsAAAALAAAACwAAAAsAAAABAAAACwAAAAEAAAALAAAAAQAAAAwAAAAAAAAADwAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAsAAAAAAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAADAAAAAAAAAANAAAAAQAAAAEAAAABAAAADwAAABEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAAPAAAAAAAAAAEAAAALAAAAAQAAAAEAAAAMAAAAAAAAAAEAAAABAAAADwAAAAAAAAAAAAAAAAAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAAAAAABAAAACwAAAAEAAAABAAAAAAAAAAAAAAABAAAAAQAAAAEAAAABAAAAAQAAAAAAAAAAAAAAAAAAAA0AAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAwAAAAAAAAACwAAAAwAAAABAAAAAQAAAAAAAAARAAAADAAAAA0AAAABAAAAAQAAAAEAAAAPAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAsAAAAAAAAADQAAABMAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAAAAAABAAAAAQAAAAAAAAABAAAAAQAAAAAAAAABAAAAAQAAAAAAAAABAAAAAQAAAAAAAAABAAAAAQAAAAsAAAAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEQAAAAwAAAAAAAAADQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAQAAAAEAAAALAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADQAAAAEAAAABAAAAAQAAAAsAAAALAAAACwAAAAsAAAALAAAACwAAAAsAAAALAAAACwAAAAsAAAALAAAACwAAAAsAAAALAAAACwAAAAsAAAALAAAADAAAAAAAAAAAAAAAAQMAY214BAAAAAEDAGNteQsAAAADAgBzbgoAWGVsJ05hZ2EgRQA=");
        
        System.out.println(ship);*/
    }
}

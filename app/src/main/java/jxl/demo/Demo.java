package jxl.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import jxl.Cell;
import jxl.Range;
import jxl.Workbook;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class Demo {
    private static final int CSVFormat = 13;
    private static final int XMLFormat = 14;
    private static Logger logger = Logger.getLogger(Demo.class);

    private static void displayHelp() {
        System.err.println("Command format:  Demo [-unicode] [-csv] [-hide] excelfile");
        System.err.println("                 Demo -xml [-format]  excelfile");
        System.err.println("                 Demo -readwrite|-rw excelfile output");
        System.err.println("                 Demo -biffdump | -bd | -wa | -write | -formulas | -features | -escher | -escherdg excelfile");
        System.err.println("                 Demo -ps excelfile [property] [output]");
        System.err.println("                 Demo -version | -logtest | -h | -help");
    }

    public static void main(String[] args) {
        char c;
        String file;
        char c2;
        boolean hideCells;
        boolean hideCells2;
        int format;
        String encoding;
        int format2;
        if (args.length == 0) {
            displayHelp();
            System.exit(1);
        }
        if (args[0].equals("-help") || args[0].equals("-h")) {
            displayHelp();
            System.exit(1);
        }
        if (args[0].equals("-version")) {
            System.out.println("v" + Workbook.getVersion());
            System.exit(0);
        }
        if (args[0].equals("-logtest")) {
            logger.debug("A sample \"debug\" message");
            logger.info("A sample \"info\" message");
            logger.warn("A sample \"warning\" message");
            logger.error("A sample \"error\" message");
            logger.fatal("A sample \"fatal\" message");
            System.exit(0);
        }
        boolean write = false;
        boolean readwrite = false;
        boolean formulas = false;
        boolean biffdump = false;
        boolean jxlversion = false;
        boolean propertysets = false;
        boolean features = false;
        boolean escher = false;
        boolean escherdg = false;
        String str = args[0];
        String outputFile = null;
        if (args[0].equals("-write")) {
            write = true;
            file = args[1];
        } else if (args[0].equals("-formulas")) {
            formulas = true;
            file = args[1];
        } else if (args[0].equals("-features")) {
            features = true;
            file = args[1];
        } else if (args[0].equals("-escher")) {
            escher = true;
            file = args[1];
        } else if (args[0].equals("-escherdg")) {
            escherdg = true;
            file = args[1];
        } else {
            if (args[0].equals("-biffdump")) {
                c = 1;
            } else if (args[0].equals("-bd")) {
                c = 1;
            } else if (args[0].equals("-wa")) {
                jxlversion = true;
                file = args[1];
            } else if (args[0].equals("-ps")) {
                propertysets = true;
                String file2 = args[1];
                propertySet = args.length > 2 ? args[2] : null;
                if (args.length == 4) {
                    outputFile = args[3];
                    file = file2;
                } else {
                    file = file2;
                }
            } else {
                if (args[0].equals("-readwrite")) {
                    c2 = 1;
                } else if (args[0].equals("-rw")) {
                    c2 = 1;
                } else {
                    file = args[args.length - 1];
                }
                readwrite = true;
                String file3 = args[c2];
                outputFile = args[2];
                file = file3;
            }
            biffdump = true;
            file = args[c];
        }
        int format3 = 13;
        boolean formatInfo = false;
        boolean hideCells3 = false;
        if (write || readwrite || formulas || biffdump || jxlversion || propertysets || features || escher || escherdg) {
            hideCells = false;
            hideCells2 = false;
            format = 13;
            encoding = "UTF8";
        } else {
            String encoding2 = "UTF8";
            int i = 0;
            while (true) {
                format2 = format3;
                int format4 = args.length;
                if (i >= format4 - 1) {
                    break;
                }
                boolean formatInfo2 = formatInfo;
                if (args[i].equals("-unicode")) {
                    encoding2 = "UnicodeBig";
                    formatInfo = formatInfo2;
                } else {
                    String encoding3 = args[i];
                    if (encoding3.equals("-xml")) {
                        format2 = 14;
                        formatInfo = formatInfo2;
                    } else if (args[i].equals("-csv")) {
                        format2 = 13;
                        formatInfo = formatInfo2;
                    } else if (args[i].equals("-format")) {
                        formatInfo = true;
                    } else if (args[i].equals("-hide")) {
                        hideCells3 = true;
                        formatInfo = formatInfo2;
                    } else {
                        System.err.println("Command format:  CSV [-unicode] [-xml|-csv] excelfile");
                        System.exit(1);
                        formatInfo = formatInfo2;
                    }
                }
                i++;
                format3 = format2;
            }
            boolean formatInfo3 = formatInfo;
            hideCells = hideCells3;
            encoding = encoding2;
            format = format2;
            hideCells2 = formatInfo3;
        }
        try {
        } catch (Throwable th) {
            t = th;
        }
        if (write) {
            new Write(file).write();
            hideCells = hideCells2;
        } else {
            if (!readwrite) {
                try {
                    if (formulas) {
                        try {
                            Workbook w = Workbook.getWorkbook(new File(file));
                            try {
                                new Formulas(w, System.out, encoding);
                                w.close();
                                return;
                            } catch (Throwable th2) {
                                t = th2;
                            }
                        } catch (Throwable th3) {
                            t = th3;
                        }
                    } else {
                        if (features) {
                            Workbook w2 = Workbook.getWorkbook(new File(file));
                            new Features(w2, System.out, encoding);
                            w2.close();
                            return;
                        }
                        if (escher) {
                            Workbook w3 = Workbook.getWorkbook(new File(file));
                            new Escher(w3, System.out, encoding);
                            w3.close();
                            return;
                        }
                        if (escherdg) {
                            Workbook w4 = Workbook.getWorkbook(new File(file));
                            new EscherDrawingGroup(w4, System.out, encoding);
                            w4.close();
                            return;
                        }
                        if (biffdump) {
                            new BiffDump(new File(file), System.out);
                            return;
                        }
                        if (jxlversion) {
                            new WriteAccess(new File(file));
                            return;
                        }
                        if (propertysets) {
                            OutputStream os = System.out;
                            if (outputFile != null) {
                                os = new FileOutputStream(outputFile);
                            }
                            new PropertySetsReader(new File(file), propertySet, os);
                            return;
                        }
                        try {
                            Workbook w5 = Workbook.getWorkbook(new File(file));
                            if (format == 13) {
                                new CSV(w5, System.out, encoding, hideCells);
                            } else if (format == 14) {
                                try {
                                    new XML(w5, System.out, encoding, hideCells2);
                                } catch (Throwable th4) {
                                    t = th4;
                                }
                            }
                            w5.close();
                            return;
                        } catch (Throwable th5) {
                            t = th5;
                        }
                    }
                } catch (Throwable th6) {
                    t = th6;
                }
                System.out.println(t.toString());
                t.printStackTrace();
                return;
            }
            ReadWrite rw = new ReadWrite(file, outputFile);
            rw.readWrite();
            hideCells = hideCells2;
        }
    }

    private static void findTest(Workbook w) {
        logger.info("Find test");
        Cell c = w.findCellByName("named1");
        if (c != null) {
            logger.info("named1 contents:  " + c.getContents());
        }
        Cell c2 = w.findCellByName("named2");
        if (c2 != null) {
            logger.info("named2 contents:  " + c2.getContents());
        }
        Cell c3 = w.findCellByName("namedrange");
        if (c3 != null) {
            logger.info("named2 contents:  " + c3.getContents());
        }
        Range[] range = w.findByName("namedrange");
        if (range != null) {
            Cell c4 = range[0].getTopLeft();
            logger.info("namedrange top left contents:  " + c4.getContents());
            Cell c5 = range[0].getBottomRight();
            logger.info("namedrange bottom right contents:  " + c5.getContents());
        }
        Range[] range2 = w.findByName("nonadjacentrange");
        if (range2 != null) {
            for (int i = 0; i < range2.length; i++) {
                Cell c6 = range2[i].getTopLeft();
                logger.info("nonadjacent top left contents:  " + c6.getContents());
                Cell c7 = range2[i].getBottomRight();
                logger.info("nonadjacent bottom right contents:  " + c7.getContents());
            }
        }
        Range[] range3 = w.findByName("horizontalnonadjacentrange");
        if (range3 != null) {
            for (int i2 = 0; i2 < range3.length; i2++) {
                Cell c8 = range3[i2].getTopLeft();
                logger.info("horizontalnonadjacent top left contents:  " + c8.getContents());
                Cell c9 = range3[i2].getBottomRight();
                logger.info("horizontalnonadjacent bottom right contents:  " + c9.getContents());
            }
        }
    }
}

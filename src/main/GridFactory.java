package main;

import lwjglutils.OGLBuffers;
import lwjglutils.OGLBuffers.Attrib;
public class GridFactory {

    /**
     * @param a počet vrcholů na řádku
     * @param b počet vrcholů ve sloupci
     * @return OGLBuffers
     */
    public static OGLBuffers generateGrid(int a, int b) {
        float[] vb = new float[a * b * 2];
        int index = 0;

        for (int j = 0; j < b; j++) {
            float y = j / (float) (b - 1);
            for (int i = 0; i < a; i++) {
//                System.out.println((i / (float) (a - 1) + " " + y));
                float x = i / (float) (a - 1);
                vb[index++] = x;
                vb[index++] = y;
            }
        }

        int[] ib = new int[(a - 1) * (b - 1) * 2 * 3];
        int index2 = 0;

        for (int r = 0; r < b - 1; r++) {
            int offset = r * a;
            for (int c = 0; c < a - 1; c++) {
                ib[index2++] = offset + c;
                ib[index2++] = offset + c + 1;
                ib[index2++] = offset + c + a;
                ib[index2++] = offset + c + 1;
                ib[index2++] = offset + c + 1 + a;
                ib[index2++] = offset + c + a;
            }
        }

        OGLBuffers.Attrib[] attributes = {
             new OGLBuffers.Attrib("inPosition", 2) // 2 floats per vertex
        };
        return new OGLBuffers(vb, attributes, ib);
    }

    public static OGLBuffers generateGridStrip(int a, int b) {
        float[] vb = new float[a * b * 2];
        int index = 0;

        for (int j = 0; j < b; j++) {
            float y = j / (float) (b - 1);
            for (int i = 0; i < a; i++) {
//                System.out.println((i / (float) (a - 1) + " " + y));
                float x = i / (float) (a - 1);
                vb[index++] = x;
                vb[index++] = y;
            }
        }
        // počet bodů + duplicitni body v generovaném troj + body uvnitř mříšky které jsou duplicitní + (-1) jelikoz by mel vzniknout bod s degen troj ale nevznika
        int pocet = (a) * (b) + (b-1) + (a-1) * (b-1)-1;
        int[] ib = new int[pocet];
        int index2 = 0;

        for (int r = 0; r < b-1; r++) {
            int offset = r * a;
            if(r%2==0){
                for (int c = 0; c < a; c++) {
                    ib[index2++] = offset + c;
                    ib[index2++] = offset + c + a;

                    //generovany troj
                    if( c==a-1){
                        ib[index2++] = offset + c + a;
                    }

                }
            }else{
                for (int c = a-1; c >= 0; c--) {

                    ib[index2++] = offset + c ;
                    ib[index2++] = offset + c + a;

                    //generovany troj
                    if( c==0){
                        ib[index2++] = offset + c + a;
                    }
                }
            }
        }

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2) // 2 floats per vertex
        };
        return new OGLBuffers(vb, attributes, ib);
    }

}

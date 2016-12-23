package com.prplplus;

import java.io.IOException;
import java.util.Base64;

import com.prplplus.shipconstruct.ShipConstructor;

public class PrplPlusTest {
    public static void main(String[] args) throws IOException {

        String dummy64 = ShipConstructor.constructDummy();

        System.out.println(dummy64);

        byte[] data = Base64.getDecoder().decode(dummy64);

        for (int i = 0; i < data.length; i++) {
            int val = data[i];
            if (val < 0)
                val += 256;
            System.out.format("%3d ", val);
            if (i % 16 == 15) {
                System.out.println();
            }
        } //*/

        System.out.println();
        System.out.println();

        for (int i = 0; i < data.length; i++) {
            int val = data[i];
            if (val < 0)
                val += 256;
            System.out.format("%02X", val);
            if (i % 2 == 1) {
                System.out.print(" ");
            }
            if (i % 16 == 15) {
                System.out.println();
            }
        } //*/
    }
}

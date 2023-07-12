package com.apkide.common;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class StoreOutputStream extends DataOutputStream {
   public StoreOutputStream(String filePath, Deflater deflater) throws IOException {
      super(new BufferedOutputStream(new DeflaterOutputStream(new FileOutputStream(filePath), deflater)));
   }

   public void writeIntArray(int[] array) throws IOException {
      for (int j : array) {
         this.writeInt(j);
      }
   }
}

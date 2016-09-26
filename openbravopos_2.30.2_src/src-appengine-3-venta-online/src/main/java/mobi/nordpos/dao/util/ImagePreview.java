/**
 * Copyright (c) 2012-2015 Nord Trading Network.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package mobi.nordpos.dao.util;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author cclafuente@gmail.com
 */
public class ImagePreview {

    public static byte[] createThumbnail(byte[] image, int size) throws IOException {
        return createThumbnail(new ByteArrayInputStream(image), size);
    }

    public static byte[] createThumbnail(InputStream in, int size) throws IOException {
       
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        
        return out.toByteArray();
    }
}

/*
 * Copyright 2017 Ellie Mae, Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its
 *     contributors may be used to endorse or promote products derived from this
 *     software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.elli.dev.sample;

import java.io.FileOutputStream;
import java.io.IOException;
import org.springframework.context.ApplicationContext;

/**
 * FileUtility class will store the supplied information as file in to the local path
 */
public class FileUtility {
    static ApplicationContext ctx;

    public boolean writeFile(byte[] byteArray) throws IOException {
        String filePath = "";

        //this is currently hard coded for the sample app. This can be from the configuration file
        filePath = "./loan_details.txt";
        System.out.println("Filepath for writing loanPipeline response =" + filePath);
        FileOutputStream stream = null;

        try {
            stream = new FileOutputStream(filePath);
            stream.write(byteArray);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());

        } finally {
            try {
                stream.close();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
        return true;
    }
}
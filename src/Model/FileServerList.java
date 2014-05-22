/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import java.util.ArrayList;

/**
 *
 * @author joel
 */
public class FileServerList extends ArrayList<String> {
    
    private String serverIp;
    
    public FileServerList(String serverIp) {
        this.serverIp = serverIp;
    }
    
    @Override
    public String toString() {
        return this.serverIp;
    }
}

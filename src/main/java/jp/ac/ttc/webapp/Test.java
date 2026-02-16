package jp.ac.ttc.webapp;

import jp.ac.ttc.webapp.adminservice.DataFormatter;

public class Test {
    public static void main(String[] args) {
        int capacity =  DataFormatter.getCapacityFromCaption("内容量: 250 g");
        System.out.print(capacity);
    }    
}

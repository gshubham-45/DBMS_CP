package com.company;

import java.io.*;
import java.util.Locale;
import java.util.Scanner;


public class Main {
    private static void create_table(String[] tokens){
        //create table emp(name,char,roll,int);
        String[] data=tokens[2].split("\\(");
        String resString=data[0];
        String[] attributes=data[1].split(",");
        int n=attributes.length;
        for(int i=0;i<n;i++){
            if(i==n-1){
                resString+="#"+attributes[n-1].substring(0,attributes[n-1].length()-2);
            }
            else{
                resString+="#"+attributes[i];
            }
        }

        try{
            Writer output = new BufferedWriter(new FileWriter("src\\db\\schema.txt", true));
            File newTable=new File("src\\tables\\"+data[0]+".txt");
            boolean status=newTable.createNewFile();
            if(status){
                output.write(resString+"\r\n");
                output.close();
                System.out.println("Table created");
            }
            else
                System.out.println("File already exists");
        }
        catch(IOException e){
            System.out.println(e);
        }
    }

    private static void insert_entries(String[] tokens) {
        //insert into emp(name,roll) values("hello",4);
    }

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        String query;
        do{
            query=sc.nextLine();
            query=query.toLowerCase();
            String[] tokens=query.split(" ");
            if(tokens[0].equals("create")){
                create_table(tokens);
            }
            else if(tokens[0].equals("insert"))
                insert_entries(tokens);
        }while(!query.equals("quit"));
    }
}

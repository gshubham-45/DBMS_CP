package com.company;

import java.io.*;
import java.util.Locale;
import java.util.Scanner;


public class Main {
    private static void create_table(String[] tokens,String query){
        //create table student(roll,int,check(roll>0),name,char(10),PRIMARY KEY(roll),FOREIGN KEY name REFERENCES emp(name));
        String[] data=query.split("\\(",2);
        String tableName=(data[0].split(" "))[2];
        String resString=tableName;
        String[] attributes=data[1].split(",");
        int n=attributes.length;
        for(int i=0;i<n;i++){
            if(i==n-1){
                if(attributes[i].contains("primary") || attributes[i].contains("foreign")){
                    if(resString.charAt(resString.length()-1)!='$')
                        resString+="$"+attributes[n-1].substring(0,attributes[n-1].length()-2);
                    else
                        resString+=attributes[n-1].substring(0,attributes[n-1].length()-2);
                }
                else
                    resString+="#"+attributes[n-1].substring(0,attributes[n-1].length()-2);
            }
            else{
                if(attributes[i].contains("check")){
                    resString+="#"+attributes[i]+"$";
                }
                else if(attributes[i].contains("primary") || attributes[i].contains("foreign")){
                    if(resString.charAt(resString.length()-1)!='$')
                        resString+="$"+attributes[i];
                    else
                        resString+=attributes[i];
                }
                else{
                    if(resString.charAt(resString.length()-1)!='$')
                        resString+="#"+attributes[i];
                    else
                        resString+=attributes[i];
                }

            }
        }

        try{
            Writer output = new BufferedWriter(new FileWriter("src\\db\\schema.txt", true));
            File newTable=new File("src\\tables\\"+tableName+".txt");
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

    private static void insert_entries(String[] tokens) throws IOException {
        //insert into emp(name,roll) values("hello",4);
        String table_name=(tokens[2].split("\\("))[0];
        File table=new File("src\\tables\\"+table_name+".txt");
        FileWriter file=new FileWriter("src\\tables\\"+table_name+".txt",true);
        if(table.exists()){
            Writer output = new BufferedWriter(file);
            String[] entries=(tokens[3].substring(7,tokens[3].length()-2)).split(",");
            String resString=entries[0];
            for(int i=1;i<entries.length;i++){
                resString+="#"+entries[i];
            }
            output.write(resString+"\r\n");
            output.close();
            System.out.println(resString);
            System.out.println("Values inserted successfully");
        }
        else
            System.out.println("Table does not exist");
    }

    private static void describe(String[] tokens) throws IOException{
        //describe emp
        File file=new File("src\\tables\\"+tokens[1]+".txt");
        if(file.exists()){
            FileReader fr=new FileReader(file);
            BufferedReader br=new BufferedReader(fr);
            String line;
            while((line=br.readLine())!=null){
                System.out.println((line.split("#"))[0]);
            }
        }
        else{
            System.out.println("Table does not exist");
        }
    }

    private static void help_tables() throws IOException {
        //help tables

        File file=new File("src\\db\\schema.txt");
        if(file.length()==0)
            System.out.println("No tables found");
        else{
            FileReader fr=new FileReader(file);
            BufferedReader br=new BufferedReader(fr);
            System.out.println("Table Names:");
            String line;
            while((line=br.readLine())!=null){
                System.out.println((line.split("#"))[0]);
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        String query;
        while(true){
            query=sc.nextLine();
            query=query.toLowerCase();
            String[] tokens=query.split(" ");
            if(tokens[0].equals("quit")){
                break;
            }
            else if(tokens[0].equals("create")){
                create_table(tokens,query);
            }
            else if(tokens[0].equals("insert")){
                try {
                    insert_entries(tokens);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(tokens[0].equals("describe")){
                try {
                    describe(tokens);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(query.equals("help tables")){
                try {
                    help_tables();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("Invalid Query");
            }
        }
    }

}

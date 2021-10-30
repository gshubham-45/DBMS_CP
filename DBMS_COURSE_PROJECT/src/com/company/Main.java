package com.company;

import javax.print.attribute.SetOfIntegerSyntax;
import java.io.*;
import java.util.*;


public class Main {
    private static void create_table(String[] tokens,String query){
        //create table student(roll,int,check(roll>0),name,char(10),PRIMARY KEY(roll),FOREIGN KEY name REFERENCES emp(name));
        String[] data=query.split("\\(",2);
        String tableName=(data[0].split(" "))[2];
        String resString=tableName+"$";
        String[] attributes=data[1].split(",");
        int n=attributes.length;
        for(int i=0;i<n;i++){
            if(i==n-1){
                if(attributes[i].contains("primary") || attributes[i].contains("foreign")){
                    if(resString.charAt(resString.length()-1)!='$')
                        resString+="$"+attributes[n-1].substring(0,attributes[n-1].length()-1);
                    else
                        resString+=attributes[n-1].substring(0,attributes[n-1].length()-1);
                }
                else
                    resString+="#"+attributes[n-1].substring(0,attributes[n-1].length()-1);
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
        //describe student
        File file=new File("src\\tables\\"+tokens[1]+".txt");
        if(file.exists()){
            File schemaFile=new File("src\\db\\schema.txt");
            FileReader fr=new FileReader(schemaFile);
            BufferedReader br=new BufferedReader(fr);
            String line;
            while((line=br.readLine())!=null){      //reading lines in schema file to get schema of required table
                String tableName=(line.split("\\$"))[0];
                if(tableName.equals(tokens[1]))
                    break;
            }

            String[] schemaTokens=line.split("\\$");
            HashSet<String> pkSet=new HashSet<>();
            HashMap<String,String> foreign=new HashMap<>();

            for(String st:schemaTokens){
                if(st.contains("primary key")){
                    st=st.substring(0,st.length()-1);
                    String[] primary=((st.split("\\("))[1]).split(",");   //Putting primary keys in a array
                    for(String p:primary)       //adding primary keys to a set
                        pkSet.add(p);
                }
                else if(st.contains("foreign key")){
                    String fk=(st.split("[\\(\\)]"))[1];        //return foreign key
                    foreign.put(fk,st);
                }
            }

            for(int i=1;i<schemaTokens.length;i++){     //creating result string
                if(schemaTokens[i].contains("primary key") || schemaTokens[i].contains("foreign key"))
                    break;
                String[] attriTokens=schemaTokens[i].split("#");
                String colName=attriTokens[0];
                String colType=attriTokens[1];
                String constraint="";
                String pk="";
                String fk=foreign.getOrDefault(colName,"");
                if(attriTokens.length==3){
                    constraint=(attriTokens[2].split("[\\(\\)]"))[0];
                }
                if(pkSet.contains(colName))
                    pk="primary key";
                String resString=colName+"--"+colType;
                if(pk!="")
                    resString+="--"+pk;
                if(fk!="")
                    resString+="--"+fk;
                if(constraint!="")
                    resString+="--"+constraint;
                System.out.println(resString);
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
                System.out.println((line.split("\\$"))[0]);
            }
        }
    }

    private static void help_command(String[] tokens) {
        if(tokens.length==3 && tokens[1].equals("create") && tokens[2].equals("table")){
            System.out.println("This query is used to create a new table by specifying attribute name,attribute data type and optional constraints like primary key and foreign key.");
            System.out.println("Syntax of CREATE TABLE :");
            System.out.println("CREATE TABLE table_name ( attribute_1 attribute1_type CHECK (constraint1),\n" +
                    "attribute_2 attribute2_type, …, PRIMARY KEY ( attribute_1, attribute_2 ),\n" +
                    "FOREIGN KEY ( attribute_y ) REFERENCES table_x ( attribute_t ), FOREIGN\n" +
                    "KEY ( attribute_w ) REFERENCES table_y ( attribute_z )… );");
        }
        else if(tokens.length==3 && tokens[1].equals("drop") && tokens[2].equals("table")){
            System.out.println("DROP is used to delete a whole database or just a table");
            System.out.println("Syntax of DROP TABLE :");
            System.out.println("DROP TABLE table_name;");
        }
        else if(tokens.length==2 && tokens[1].equals("select")){
            System.out.println("SELECT is used to select certain columns of table using different conditions specified using WHERE clause.");
            System.out.println("Syntax of SELECT :");
            System.out.println("SELECT attribute_list FROM table_list WHERE condition_list;");
        }
        else if(tokens.length==2 && tokens[1].equals("insert")){
            System.out.println("INSERT INTO clause is used to insert tuples in specified table.");
            System.out.println("Syntax of INSERT :");
            System.out.println("INSERT INTO table_name VALUES ( val1, val2, … );");
        }
        else if(tokens.length==2 && tokens[1].equals("delete")){
            System.out.println("DELETE clause is used to delete tuples from table based on some condition");
            System.out.println("Syntax of DELETE :");
            System.out.println("DELETE FROM table_name WHERE condition_list;");
        }
        else if(tokens.length==2 && tokens[1].equals("update")){
            System.out.println("UPDATE clause is used to update or modify particular attribute value based on some condition.");
            System.out.println("Syntax of UPDATE :");
            System.out.println("UPDATE table_name SET attr1 = val1, attr2 = val2… WHERE condition_list;");
        }
        else{
            System.out.println("Query is invalid!!");
        }
    }

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        String query;
        while(true){
            query=sc.nextLine();
            query=query.toLowerCase();
            if(query.charAt(query.length()-1)!=';'){
                System.out.println("; missing");
                continue;
            }
            query=query.substring(0,query.length()-1);      //removing semi-colon from query
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
            else if(tokens[0].equals("help")){
                help_command(tokens);
            }
            else{
                System.out.println("Invalid Query");
            }
        }
    }
}

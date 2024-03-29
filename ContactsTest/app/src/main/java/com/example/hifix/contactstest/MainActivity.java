package com.example.hifix.contactstest;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
private ListView contactsview;
ArrayAdapter<String>  adapter;
List<String> contactList=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
contactsview=(ListView)findViewById(R.id.contacts_view);
adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,contactList);
contactsview.setAdapter(adapter);
readcontacts();
    }
private void readcontacts(){
    Cursor cursor=null;
    try{
cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
while(cursor.moveToNext()){
    String displayname=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
    String number=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    contactList.add(displayname+"\n"+number);
}
        adapter.notifyDataSetChanged();
    }catch (Exception e){
e.printStackTrace();
    }finally {
        if(cursor!=null){

            cursor.close();

        }
    }





}


}

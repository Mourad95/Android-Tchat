package com.example.boss.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Boss on 25/01/2018.
 */



public class Chat_room extends AppCompatActivity {

    private Button btn_send_msg;
    private EditText input_msg;
    private TextView chat_conversation;


    private String user_name, room_name;
    private DatabaseReference root;
    private String temp_key;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        btn_send_msg = (Button) findViewById(R.id.btn_send);
        input_msg = (EditText) findViewById(R.id.editText);
        chat_conversation = (TextView) findViewById(R.id.textView);


        user_name = getIntent().getExtras().get("user_name").toString();  // Récuperation du nom utilisateur.
        room_name = getIntent().getExtras().get("room_name").toString();  // Récuperation du nom du salon.
        setTitle(" Room - "+room_name);

        root = FirebaseDatabase.getInstance().getReference().child(room_name); // Création d'une instance DatabaseReference qui est le point de départ de toute les operations effectuer sur la base de donnée Firebase.
                                                                               // getInstance() permet d'avoir une "vue sur une instance, getReference() permet d'acceder a la database en lecture et en écriture.
                                                                               // Retourne la racine de la structure.

        btn_send_msg.setOnClickListener(new View.OnClickListener() { // Methode de "Clic" qui renvoi a la methode Onclick().
            @Override
            public void onClick(View view) {

                Map<String,Object> map = new HashMap<String,Object>(); // Map qui permet de recevoir les messages.
                temp_key = root.push().getKey(); // getKey retourne le message et initialise temp_key.
                root.updateChildren(map);// Mise a jours des champs de la map

                DatabaseReference message_root = root.child(temp_key); // Recuperation des données de hashMap "map".
                Map<String,Object> map2 = new HashMap<String, Object>(); // Création de la HashMap "map2"
                map2.put("name",user_name); // initialisation de la hashmap avec le nom de l'utilisateur.
                map2.put("msg", input_msg.getText().toString()); // initialisation de la hashmap avec le message de l'utilisateur.

                message_root.updateChildren(map2); // mise a niveau des données de la map avec les données de la map2

            }
        });

            root.addChildEventListener(new ChildEventListener() {  // root implémentant cette interface pour recevoir des événements sur les changements dans les emplacements enfants d'une référence Firebase la méthode appropriée sera déclenchée lorsque des modifications se produisent.


                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) { // Cette méthode est déclenchée lorsqu'un nouvel "enfant = nouvelle ligne dans firebase" est ajouté à l'emplacement auquel cet écouteur a été ajouté.

                    append_chat_conversation(dataSnapshot); // appel de la methode append_chat_conversation() >>> la méthode est en bas du programme
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    append_chat_conversation(dataSnapshot);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    private String chat_msg, chat_user_name;
    private void append_chat_conversation(DataSnapshot dataSnapshot){

        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()){                                        // Methode qui permet de poster son message.
            chat_msg = (String)((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();

            chat_conversation.append(chat_user_name +" : "+chat_msg +" \n"); // append permet d'ajouté les élements.
        }
    }

}

package com.example.boss.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button add_room;
    private EditText room_name;

    private ListView listview;
    private ArrayAdapter<String> arrayAdapter;  // initialisation d'un ArrayAdapter celui ci reçois la liste les differents salons du tChat.
    private ArrayList<String> list_of_rooms = new ArrayList<>(); // Liste des salons du tChat.
    private String name;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot(); // Création d'une instance DatabaseReference qui est le point de départ de toute les operations effectuer sur la base de donnée Firebase.
                                                                                              // getInstance() permet d'avoir une "vue sur une instance, getReference() permet d'acceder a la database en lecture et en écriture.
    @Override                                                                                 // Retourne la racine de la structure.
    protected void onCreate(Bundle savedInstanceState) { // Methode onCreate() point d'entrée du programme.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Methode setContentView() qui permet d'afficher le Layout.

        add_room = (Button) findViewById(R.id.btn_add_room);                    // Définis variable >> boutton.
        room_name = (EditText) findViewById(R.id.room_name_editText);           // Définis variable >> room name editText.
        listview = (ListView) findViewById(R.id.listView);                      // Définis variable >> listView.

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_rooms); // Création d'une instance ArrayAdapter celui ci reçois la liste les differents salon du tChat.

        listview.setAdapter(arrayAdapter ); // setAdapter() Définit l'adaptateur (arrayAdapter) qui fournit les données et les vues pour représenter les données dans le widget(listview).

        request_user_name(); // Appel de la methode request user name >>>> tout en bas du programme.

        add_room.setOnClickListener(new View.OnClickListener() { // setOnClickListener "Ecouteur de clic" est une methode qui définis se que le bouton fait quand on clic dessus.
            @Override                                            // On clique sur "add_room" et cela appel la methode onClick() qui met a jour la vue après la création de la nouvelle chambre du tchat
            public void onClick(View view) {

                Map<String,Object> map  = new HashMap<String, Object>();
                map.put(room_name.getText().toString(),"");
                root.updateChildren(map); // Mise a jours des champs de la map

            }
        });

        root.addValueEventListener(new ValueEventListener() { // addValueEventListener continue à écouter la requête ou la référence de la base de données, la ou il est attaché.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { // DataSnapshot contient des données d'une base de donnée Firebase,
                                                                  // A chaque fois qu'on lis DataBase data, les données sont retourné via une instance de DataSnapshot.
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator(); // Création d'un Iterator i, getChildren donne accées a tout les "enfants" immediat de la donnée.


                while(i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());  // La methode set() initialise add avec les données retournées dans DataSnapshot, i.next() est une methode de java
                }                                                // qui permet de passer au prochain element du tableau, la methode getkey() renseigne add sur l'element.
                list_of_rooms.clear();              // On efface le tableau.
                list_of_rooms.addAll(set);          // On valorise le tableau avec les élements retournées.

                arrayAdapter.notifyDataSetChanged(); //Notifie a l'arrayAdapter attachés que les données sous-jacentes ont été modifiées et que toute vue reflétant l'ensemble de données devrait s'actualiser.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { // Les instances de DatabaseError sont transmises lorsqu'une opération échoue. Ils contiennent une description de l'erreur spécifique qui s'est produite.

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() { //
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(),Chat_room.class); // Intent intent permet le demarrage explicite ou implicite d'une Activity.
                intent.putExtra("room_name",((TextView)view).getText().toString());
                intent.putExtra("user_name",name);
                startActivity(intent);
            }
        });

    }

    private void request_user_name(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this); // Créer une alerte boite de dialogue.
        builder.setTitle("Enter name:");

        final EditText input_field = new EditText(this);  // Valorise notre instance input_field avec notre entrée a la demande du prénom.

        builder.setView(input_field);  // Classe Builder pour la production d'éléments complexes avec des paires de champs et de noms correspondants. Le constructeur commence vide. L'ordre dans lequel les éléments sont ajoutés est conservé pour la mise en page en mémoire.
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // Quand on clique sur Ok la méthode ci contre s'execute.
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                name = input_field.getText().toString(); // valorise name avec l'entrée de l'utilisateur.
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // La methode définis la méthode a appelé quand on appuis le mauvais bouton ou si l'on donne une mauvaise entrée.
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                request_user_name();
            }
        });

        builder.show(); // affiche le retour de la methode request_user_name
    }

}

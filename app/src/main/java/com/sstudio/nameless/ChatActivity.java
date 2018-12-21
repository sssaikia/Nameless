package com.sstudio.nameless;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity {
    DatabaseReference rootRef, reference, presence,presenceOnTag, me,meOnTag,tags,favs;
    ListView listView;
    ImageButton imageButton;
    EmojiconEditText editText;
    FirebaseListAdapter firebaseListAdapter;
    FirebaseRecyclerAdapter<String,ViewHolderRecycler> tagAdapter,favAdapter;
    TextView textView;
    String tag = "Nameless";
    private EmojIconActions emojIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rootRef = FirebaseDatabase.getInstance().getReference();
        reference = rootRef.child("chatsurl").child(tag);
        favs = rootRef.child("favTags").child((FirebaseAuth.getInstance()).getCurrentUser().getUid().toString());
        presence = rootRef.child("online");
        tags=rootRef.child("Tags");




        me = presence.push();
        me.push().setValue("online");
        me.onDisconnect().removeValue();
        editText = findViewById(R.id.editText);
        imageButton = findViewById(R.id.sendButton);
        listView = findViewById(R.id.chats);
        LinearLayout linlay=findViewById(R.id.chatLay);





        EmojIconActions  emojIcon=new EmojIconActions(this,linlay,editText,((ImageView) findViewById(R.id.emojiB)));
        emojIcon.ShowEmojIcon();
        emojIcon.setUseSystemEmoji(true);
        editText.setUseSystemDefault(true);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard","open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard","close");
            }
        });

        new AlertDialog.Builder(this, R.style.dialog)
                .setTitle("Welcome to Nameless")
                .setIcon(R.drawable.nameless)
                .setMessage("Nameless is just for fun and entertainment purpose only." +
                        " Under no circumstance the developer is responsible." +
                        " Click I Agree to acknowledge  that you are using Nameless at your own risk. ")
                .setPositiveButton("I Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       favTags();
                    }
                })
                .setNegativeButton("Do not agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setCancelable(false).show();

        presence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView) findViewById(R.id.onlineCount)).setText(dataSnapshot.getChildrenCount() + " Online");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firebaseListAdapter = new FirebaseListAdapter<User>(this, User.class, R.layout.listtextlay, reference) {
            @Override
            protected void populateView(View view, User s, int i) {
                textView= view.findViewById(R.id.listText);
                if (s.getId().equals((FirebaseAuth.getInstance()).getCurrentUser().getEmail())){
                    textView.setText(s.getText());
                    textView.setGravity(Gravity.END);
                }else {
                    textView.setText(s.getText());
                    textView.setGravity(Gravity.START);
                }

            }

        };
        listView.setAdapter(firebaseListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(ChatActivity.this, R.style.dialog)
                        .setTitle("Delete message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i1) {
                                try {
                                    User n=new User();
                                    n= (User) firebaseListAdapter.getItem(i);
                                    Log.d(" blah blah   ::   ",""+n.getId());
                                    if (n.getId().equals((FirebaseAuth.getInstance()).getCurrentUser().getEmail())){
                                        firebaseListAdapter.getRef(i).removeValue();
                                    } else {
                                        Toast.makeText(ChatActivity.this, "Only the sender can delete their texts.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Log.e("Exception deleting :: ", "" + e);
                                }
                            }
                        }).setNegativeButton("No", null)
                        .show();                return true;
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim().length() != 0) {
                    User user=new User(editText.getText().toString(),(FirebaseAuth.getInstance()).getCurrentUser().getEmail());
                    reference.push().setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            //Toast.makeText(ChatActivity.this, ""+databaseError, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                editText.setText("");
            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.sstudio);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setBackgroundResource(R.drawable.dialogback);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ChatActivity.this, "An sstudio product", Toast.LENGTH_SHORT).show();
                }
            });
            new AlertDialog.Builder(this, R.style.dialog)
                    .setTitle("About Nameless")
                    .setIcon(R.drawable.nameless)
                    .setView(imageView)
                    .setMessage("Nameless is created just for entertainment purpose only." +
                            "The motive is to help people share feelings and discuss matters directly" +
                            " with people without exposing the identity." +
                            "Here you can say the name of 'you know who', " +
                            "and he can wish to find you." +
                            "Have fun and enjoy being in the dark.")
                    .setPositiveButton("Got it", null)
                    .show();
        }
        if (item.getItemId() == R.id.help) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                new AlertDialog.Builder(this, R.style.dialog)
                        .setTitle("Help?")
                        .setMessage(Html.fromHtml("Do you really need help? Doesn't seem like. " +
                                "Anyway,\n" +
                                "1. Here can chat with people you know or not.\n" +
                                "2. You can discuss about sports,politics,movies,songs or whatever you want." +
                                "The only thing that stands is the right #tag." +
                                " Since the chats are anonymous you can also share your secrets" +
                                " that's been bugging you for days or weeks or a year?" +
                                "\n3. Here is the main deal. Say you just sent something " +
                                "which can expose your identity, like your name or phone number " +
                                "or something, you can delete it right away. " +
                                "Long press on the texts and click ok.\n" +
                                "\n\nWas it helpfull?\n" +
                                "Share your thaughts on <b>#NamelessFeedback</b>" +
                                "",Html.FROM_HTML_MODE_LEGACY)).setPositiveButton("Great", null).show();
            }else{
                new AlertDialog.Builder(this, R.style.dialog)
                        .setTitle("Help?")
                        .setMessage(Html.fromHtml("Do you really need help? Doesn't seem like. " +
                                "Anyway,\n" +
                                "1. Here can chat with people you know or not.\n" +
                                "2. You can discuss about sports,politics,movies,songs or whatever you want." +
                                "The only thing that stands is the right #tag." +
                                " Since the chats are anonymous you can also share your secrets" +
                                " that's been bugging you for days or weeks or a year?" +
                                "\n3. Here is the main deal. Say you just sent something " +
                                "which can expose your identity, like your name or phone number " +
                                "or something, you can delete it right away. " +
                                "Long press on the texts and click ok.\n" +
                                "\n\nWas it helpfull?\n" +
                                "Share your thaughts on <b>#NamelessFeedback</b>" +
                                "")).setPositiveButton("Great", null).show();
            }
        }
        if (item.getItemId() == R.id.faq) {
            new AlertDialog.Builder(this, R.style.dialog)
                    .setTitle("FAQ?")
                    .setMessage("There is no faq ready yet.")
                    .setPositiveButton("Hmm", null)
                    .show();
        }
        if (item.getItemId() == R.id.tag) {
            getHtag();
        }
        if (item.getItemId()==R.id.logout){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                new AlertDialog.Builder(this,R.style.dialog)
                        .setTitle("Logout?")
                        .setMessage(Html.fromHtml("You will be logged out on clicking ok.\n" +
                                " You can come back at any time. All your messages are saved. " +
                                "All you have to do is just type in the same #tag" +
                                " which is <b>#"+tag+"</b>",Html.FROM_HTML_MODE_LEGACY))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                (FirebaseAuth.getInstance()).signOut();
                                Intent intent=new Intent(ChatActivity.this,LoginActivity.class);
                                startActivity(intent);
                                try {
                                    me.removeValue();
                                }catch (Exception e){
                                    Log.d("exception  ::   ",e+"");
                                }
                                try {
                                    meOnTag.removeValue();
                                }catch (Exception e){
                                    Log.d("exception  ::   ",e+"");
                                }
                                finish();
                            }
                        }).setNegativeButton("No",null).show();
            }else {
                new AlertDialog.Builder(this,R.style.dialog)
                        .setTitle("Logout?")
                        .setMessage(Html.fromHtml("You will be logged out on clicking ok.\n" +
                                " You can come back at any time. All your messages are saved. " +
                                "All you have to do is just type in the same #tag" +
                                " which is <b>#"+tag+"</b>"))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                (FirebaseAuth.getInstance()).signOut();
                                Intent intent=new Intent(ChatActivity.this,LoginActivity.class);
                                startActivity(intent);
                                try {
                                    me.removeValue();
                                }catch (Exception e){
                                    Log.d("exception  ::   ",e+"");
                                }
                                try {
                                    meOnTag.removeValue();
                                }catch (Exception e){
                                    Log.d("exception  ::   ",e+"");
                                }
                                finish();
                            }
                        }).setNegativeButton("No",null).show();
            }

        }
        if (item.getItemId()==R.id.favadd){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                new AlertDialog.Builder(this,R.style.dialog)
                        .setTitle("Add to fav?")
                        .setMessage(Html.fromHtml("Click ok to add <b>#"+tag+"</b> to your favorite list."
                                ,Html.FROM_HTML_MODE_LEGACY))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                favs.push().setValue(tag, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError==null){
                                            Toast.makeText(ChatActivity.this, "Added to favorite.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("No",null).show();
            }else {
                new AlertDialog.Builder(this,R.style.dialog)
                        .setTitle("Add to fav?")
                        .setMessage(Html.fromHtml("Click ok to add <b>#"+tag+"</b> to your favorite list."))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                favs.push().setValue(tag, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError==null){
                                            Toast.makeText(ChatActivity.this, "Added to favorite.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("No",null).show();
            }
        }
        return true;
    }


    public void favTags(){
        final View v=this.getLayoutInflater().inflate(R.layout.tag_layout,null);
        favAdapter=new FirebaseRecyclerAdapter<String, ViewHolderRecycler>(String.class,R.layout.tag_pallete_lay,
                ViewHolderRecycler.class,favs) {
            @Override
            protected void populateViewHolder(ViewHolderRecycler viewHolder, String model, int position) {
                viewHolder.textView.setText("#"+model);
            }
        };
        RecyclerView s=v.findViewById(R.id.stextag1);
        s.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.HORIZONTAL));
        s.setAdapter(favAdapter);
        EditText editText1 = v.findViewById(R.id.addTag);
        editText1.setVisibility(View.GONE);
        AlertDialog.Builder alert=new AlertDialog.Builder(ChatActivity.this, R.style.dialog);
        alert.setTitle("Select #tag")
                .setView(v)
                .setIcon(R.drawable.nameless)
                .setPositiveButton("More", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getHtag();
                    }
                }).setCancelable(false).create();
            alert.setMessage("Click on MORE to see available #tags or to create your own.");
        final AlertDialog b=alert.create();
        b.show();
        s.addOnItemTouchListener( // and the click is handled
                new RecyclerItemClick(this, new RecyclerItemClick.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position) {
                        Log.d("click item", String.valueOf(position));
                        TextView textView = (TextView) view.findViewById(R.id.pallete);
                        Log.d("test",textView.getText().toString());
                        tag=textView.getText().toString().replace("#","");
                        //Toast.makeText(ChatActivity.this, adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            new AlertDialog.Builder(ChatActivity.this,R.style.dialog)
                                    .setTitle(Html.fromHtml("<b>#"+tag+"</b> selected.",Html.FROM_HTML_MODE_LEGACY))
                                    .setPositiveButton("load", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            reference = rootRef.child("chatsurl").child(tag);
                                            reload();
                                            tags.child(tag).setValue(tag);
                                            b.cancel();
                                        }
                                    }).setNegativeButton("remove from fav", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    favAdapter.getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            Toast.makeText(ChatActivity.this, "Removed from fav.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).show();
                        }else {
                            new AlertDialog.Builder(ChatActivity.this,R.style.dialog)
                                    .setTitle(Html.fromHtml("<b>#"+tag+"</b> selected."))
                                    .setPositiveButton("load", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            reference = rootRef.child("chatsurl").child(tag);
                                            reload();
                                            tags.child(tag).setValue(tag);
                                            b.cancel();
                                        }
                                    }).setNegativeButton("remove from fav", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    favAdapter.getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            Toast.makeText(ChatActivity.this, "Removed from fav.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).show();
                        }
                    }
                }));
    }



    public void getHtag() {
        try {
            meOnTag.removeValue();
        }catch (Exception e){
            Log.e("remove value on getHtag",e+"");
        }
        final View v=this.getLayoutInflater().inflate(R.layout.tag_layout,null);
        tagAdapter=new FirebaseRecyclerAdapter<String, ViewHolderRecycler>(String.class,R.layout.tag_pallete_lay,
                ViewHolderRecycler.class,tags) {
            @Override
            protected void populateViewHolder(ViewHolderRecycler viewHolder, String model, int position) {
                viewHolder.textView.setText("#"+model);
            }
        };

        final EditText editText = v.findViewById(R.id.addTag);
        RecyclerView s=v.findViewById(R.id.stextag1);
        s.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.HORIZONTAL));
        s.setAdapter(tagAdapter);
        editText.setBackgroundResource(R.drawable.edback);
        AlertDialog.Builder alert=new AlertDialog.Builder(ChatActivity.this, R.style.dialog);
                alert.setTitle("Select #tag")
                .setView(v)
                .setIcon(R.drawable.nameless)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {  if (editText.getText().toString().trim().length() != 0) {
                            tag = editText.getText().toString().trim();
                            tag = tag.replaceAll("#", "");
                            Log.d(" String path  ::  ", tag);
                            reference = rootRef.child("chatsurl").child(tag);
                            tags.child(tag).setValue(tag);
                            reload();
                        }
                            }catch (Exception e){
                                Toast.makeText(ChatActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                            getHtag();
                            }


                    }
                }).setCancelable(false).create();
        final AlertDialog b=alert.create();
        b.show();
        s.addOnItemTouchListener( // and the click is handled
                new RecyclerItemClick(this, new RecyclerItemClick.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Log.d("click item", String.valueOf(position));
                        TextView textView = (TextView) view.findViewById(R.id.pallete);
                        Log.d("test",textView.getText().toString());
                        tag=textView.getText().toString().replace("#","");
                        //Toast.makeText(ChatActivity.this, adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
                        reference = rootRef.child("chatsurl").child(tag);
                        reload();
                        tags.child(tag).setValue(tag);
                        b.cancel();
                    }
    }));
    }
    String lastTag="Nameless";
    public void reload() {
        lastTag=tag;
        presenceOnTag = rootRef.child("OnlineCountOnTag").child(lastTag);
        meOnTag = presenceOnTag.push();
        meOnTag.push().setValue("online");
        meOnTag.onDisconnect().removeValue();

        firebaseListAdapter = new FirebaseListAdapter<User>(this, User.class, R.layout.listtextlay, reference) {
            @Override
            protected void populateView(View view, User s, int i) {
                //Toast.makeText(mContext, s.getText()+"  "+s.getId(), Toast.LENGTH_SHORT).show();
                textView= view.findViewById(R.id.listText);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //params.weight = 1.0f;

                if (s.getId().equals((FirebaseAuth.getInstance()).getCurrentUser().getEmail())){
                    textView.setText(s.getText());
                    params.gravity = Gravity.END;
                    textView.setLayoutParams(params);
                }else {
                    textView.setText(s.getText());
                    params.gravity = Gravity.START;
                    textView.setLayoutParams(params);
                }
            }

        };
        listView.setAdapter(firebaseListAdapter);

        presenceOnTag.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView)findViewById(R.id.onlineOnTag)).setText(dataSnapshot.getChildrenCount()+" Online on this chat");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ((TextView)findViewById(R.id.tagTV)).setText("#"+tag);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this,R.style.dialog)
                .setTitle("Quit?")
                .setMessage("Do you really want to leave?\n" +
                        "You will still be logged in.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setNegativeButton("No",null)
                .show();
    }
}

package com.msk.taf.info;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.msk.taf.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;

/**
 * Created by msk on 14/06/16.
 */

public class EscolhePasta extends ListActivity {

    public static final String START_DIR = "startDir";
    public static final String ONLY_DIRS = "onlyDirs";
    public static final String SHOW_HIDDEN = "showHidden";
    public static final String CHOSEN_DIRECTORY = "chosenDir";
    public static final int PICK_DIRECTORY = 43522432;
    private File dir;
    private String[] pastas;
    private boolean showHidden = false;
    private boolean onlyDirs = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        String strSDCardPath = System.getenv("SECONDARY_STORAGE");
        if ((strSDCardPath == null) || strSDCardPath.length() == 0){
            strSDCardPath = System.getenv("EXTERNAL_SDCARD_STORAGE");
        }
        if (strSDCardPath != null){
            if (strSDCardPath.contains(":")){
                strSDCardPath = strSDCardPath.substring (0, strSDCardPath.indexOf(":"));
            }

            File externalFilePath = new File (strSDCardPath);
            if (externalFilePath.exists() && externalFilePath.canWrite()){
                dir = externalFilePath.getParentFile();
            } else {

                dir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).getParentFile().getParentFile().getParentFile();
            }

        } else {

            dir = Environment.getExternalStorageDirectory();
        }

        if (extras != null) {
            String preferredStartDir = extras.getString(START_DIR);
            showHidden = extras.getBoolean(SHOW_HIDDEN, false);
            onlyDirs = extras.getBoolean(ONLY_DIRS, true);
            if (preferredStartDir != null) {
                File startDir = new File(preferredStartDir);
                if (startDir.isDirectory()) {
                    dir = startDir;
                }
            }
        }

        setContentView(R.layout.lista_pastas);
        setTheme(R.style.TemaApp);
        setTitle(dir.getAbsolutePath());
        Button btnChoose = (Button) findViewById(R.id.btnChoose);
        String name = dir.getName();
        if (name.length() == 0)
            name = "/";
        btnChoose.setText(" Salvar em '" + name + "'");
        btnChoose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                returnDir(dir.getAbsolutePath());
            }
        });

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        if (!dir.canRead()) {
            Toast.makeText(getApplicationContext(), "Acesso negado",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final ArrayList<File> files = filter(dir.listFiles(), onlyDirs, showHidden);

        pastas = names(files);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {

            public View getView(int position, View convertView, ViewGroup parent) {

                LayoutInflater inflater = getLayoutInflater();
                View rowView = inflater.inflate(R.layout.linha_pastas, null);

                TextView tv = (TextView) rowView.findViewById(R.id.tvPasta);
                AppCompatImageView iv = (AppCompatImageView) rowView.findViewById(R.id.ivFolder);
                iv.setImageResource(R.drawable.ic_folder);

                String str = pastas[position];
                tv.setText(str);

                return rowView;
            }

            @Override
            public int getCount() {
                return files.size();
            }

            @Override

            public long getItemId(int posicao) {
                return posicao;
            }

        };
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!files.get(position).isDirectory())
                    return;
                String path = files.get(position).getAbsolutePath();
                Intent intent = new Intent(EscolhePasta.this, EscolhePasta.class);
                intent.putExtra(EscolhePasta.START_DIR, path);
                intent.putExtra(EscolhePasta.SHOW_HIDDEN, showHidden);
                intent.putExtra(EscolhePasta.ONLY_DIRS, onlyDirs);
                startActivityForResult(intent, PICK_DIRECTORY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_DIRECTORY && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            String path = (String) extras.get(EscolhePasta.CHOSEN_DIRECTORY);
            returnDir(path);
        }
    }

    private void returnDir(String path) {

        SharedPreferences sharedPref = getSharedPreferences("backup", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("backup", path);
        editor.commit();

        Intent result = new Intent();
        result.putExtra(CHOSEN_DIRECTORY, path);
        setResult(RESULT_OK, result);
        finish();
    }

    public ArrayList<File> filter(File[] file_list, boolean onlyDirs, boolean showHidden) {
        ArrayList<File> files = new ArrayList<File>();
        for (File file : file_list) {
            if (onlyDirs && !file.isDirectory())
                continue;
            if (!showHidden && file.isHidden())
                continue;
            if (!file.canRead())
                continue;
            files.add(file);
        }
        Collections.sort(files);
        return files;
    }

    public String[] names(ArrayList<File> files) {
        String[] names = new String[files.size()];
        int i = 0;
        for (File file : files) {
            names[i] = file.getName();
            i++;
        }
        return names;
    }
}

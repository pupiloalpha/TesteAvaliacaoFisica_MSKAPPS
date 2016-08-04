package com.msk.taf.info;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.msk.taf.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class EscolhePasta extends ListActivity {

    private static final String START_DIR = "startDir";
    public static final String CHOSEN_DIRECTORY = "chosenDir";
    private static final int PICK_DIRECTORY = 4352;
    private File dir;
    private String[] pastas;
    private String tipo;
    private boolean showHidden = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        tipo = extras.getString("tipo");
        String preferredStartDir = extras.getString(START_DIR);

        if (preferredStartDir != null) {
            if (new File(preferredStartDir).isDirectory()) {
                dir = new File(preferredStartDir);
            }
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                dir = new File("/");
            } else {
                dir = Environment.getExternalStorageDirectory().getParentFile().getParentFile();
            }
        }

        setContentView(R.layout.lista_pastas);
        setTitle(dir.getAbsolutePath());

        TextView sem = (TextView) findViewById(R.id.tvSemTestes);

        Button btnChoose = (Button) findViewById(R.id.btnChoose);
        if (!tipo.equals("")) {
            btnChoose.setVisibility(View.GONE);
        } else {
            String name = dir.getName();
            if (name.length() == 0)
                name = "/";
            btnChoose.setText(" Salvar em '" + name + "'");
            btnChoose.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    returnDir(dir.getAbsolutePath());
                }
            });
        }

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        if (!dir.canRead()) {
            Toast.makeText(getApplicationContext(), "Acesso negado",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final ArrayList<File> files = filter(dir.listFiles());

        pastas = names(files);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {

            public View getView(int position, View convertView, ViewGroup parent) {

                LayoutInflater inflater = getLayoutInflater();
                View rowView = inflater.inflate(R.layout.linha_pastas, null);

                TextView tv = (TextView) rowView.findViewById(R.id.tvPasta);
                TextView data = (TextView) rowView.findViewById(R.id.tvData);
                AppCompatImageView iv = (AppCompatImageView) rowView.findViewById(R.id.ivFolder);

                String str = pastas[position];
                tv.setText(str);

                if (!files.get(position).isDirectory()) {
                    iv.setImageResource(R.drawable.ic_archive);
                    iv.setColorFilter(getResources().getColor(R.color.verde));

                    Date lastModified = new Date(files.get(position).lastModified());
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String formattedDateString = formatter.format(lastModified);
                    data.setText(formattedDateString);
                } else {
                    iv.setImageResource(R.drawable.ic_folder);
                    iv.setColorFilter(getResources().getColor(R.color.cinza_escuro));
                    data.setVisibility(View.GONE);

                }

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
        lv.setEmptyView(sem);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (files.get(position).isDirectory()) {
                    String path = files.get(position).getAbsolutePath();
                    Intent intent = new Intent(EscolhePasta.this, EscolhePasta.class);
                    intent.putExtra(EscolhePasta.START_DIR, path);
                    intent.putExtra("tipo", tipo);
                    startActivityForResult(intent, PICK_DIRECTORY);
                } else {
                    String path = files.get(position).getAbsolutePath();
                    if (path.endsWith(tipo)) {
                        Intent result = new Intent();
                        result.putExtra(CHOSEN_DIRECTORY, path);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                }
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

        Intent result = new Intent();
        result.putExtra(CHOSEN_DIRECTORY, path);
        setResult(RESULT_OK, result);
        finish();
    }

    private ArrayList<File> filter(File[] file_list) {
        ArrayList<File> pastas = new ArrayList<File>();
        for (File file : file_list) {
            if (!file.isDirectory())
                continue;
            if (file.isHidden())
                continue;
            if (!file.canRead())
                continue;
            if (!file.canWrite())
                continue;
            pastas.add(file);
        }
        Collections.sort(pastas);

        ArrayList<File> xls = new ArrayList<File>();
        if (!tipo.equals("")) {
            for (File file : file_list) {
                if (file.isDirectory())
                    continue;
                if (!file.getAbsolutePath().endsWith(tipo))
                    continue;
                if (file.isHidden())
                    continue;
                if (!file.canRead())
                    continue;
                xls.add(file);
            }
            Collections.sort(xls);
        }

        ArrayList<File> files = new ArrayList<File>();
        for (int i = 0; i < pastas.size(); i++) {
            files.add(pastas.get(i));
        }
        for (int j = 0; j < xls.size(); j++) {
            files.add(xls.get(j));
        }
        return files;
    }

    private String[] names(ArrayList<File> files) {
        String[] names = new String[files.size()];
        int i = 0;
        for (File file : files) {
            names[i] = file.getName();
            i++;
        }
        return names;
    }
}

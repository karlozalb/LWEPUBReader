package com.projectclean.lwepubreader.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.adapters.CustomSpinnerAdapter;
import com.projectclean.lwepubreader.model.Book;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Carlos Albaladejo Pérez on 16/02/2016.
 */
public class BookDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String EXTRA_ID = "A_EXTRA_ID";

    private Book mBook;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_book_details_activity);

        long bookId = getIntent().getExtras().getLong(EXTRA_ID);

        List<Book> books = Book.find(Book.class,"ID = "+bookId);

        if (books != null && books.size() > 0){
            Book b = books.get(0);
            TextView tvAuthor = (TextView)findViewById(R.id.book_details_authors);
            tvAuthor.setText(b.getAuthor());
            TextView tvTitle = (TextView)findViewById(R.id.book_details_book_name);
            tvTitle.setText(b.getTitle());
            TextView tvDateAdded = (TextView)findViewById(R.id.book_details_date_added);
            tvDateAdded.setText(b.getDateAdded());
            TextView tvDateRead = (TextView)findViewById(R.id.book_details_last_read);
            tvDateRead.setText(b.getDateLastRead());

            ImageView bookCover = (ImageView)findViewById(R.id.book_details_book_cover);

            Picasso.with(this).load(new File(getFilesDir().getAbsolutePath() + "/" + b.getBookCover())).resize(400, 532).into(bookCover);

            mBook = b;
        }

        Spinner spinner = (Spinner) findViewById(R.id.languages_list);
        // Create an ArrayAdapter using the string array and a default spinner layout
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages_list, android.R.layout.simple_spinner_item);
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this);
        adapter.setItems(getResources().getStringArray(R.array.languages_list));

        // Specify the layout to use when the list of choices appears
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        spinner.setSelection(mBook.getTranslationConfiguration());

        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mBook.setTranslationConfiguration(position);
        mBook.save();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

/*
 * Copyright ${year} Andrew Reitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shillelagh;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import com.example.shillelagh.model.SimpleObject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryBuilderTest extends AndroidTestCase {

  private Shillelagh shillelagh;

  @Override protected void setUp() throws Exception {
    super.setUp();

    SQLiteOpenHelper sqliteOpenHelper = new QueryBuilderTestSQLiteOpenHelper(getContext());
    shillelagh = new Shillelagh(sqliteOpenHelper);

    for (int i = 0; i < 1000; i++) {
      final SimpleObject simpleObject =
          new SimpleObject(String.valueOf(i), String.valueOf((i % 10) + 1), (i % 100) + 1);
      shillelagh.insert(simpleObject);
    }
  }

  @Override protected void tearDown() throws Exception {
    getContext().deleteDatabase(QueryBuilderTestSQLiteOpenHelper.DATABASE_NAME);
    super.tearDown();
  }

  public void testSelect() {
    final WhereBuilder<SimpleObject> builder = shillelagh.selectFrom(SimpleObject.class);

    assertThat(builder.toString()).isEqualTo("SELECT * FROM SimpleObject");
    assertThat(builder.toList()).hasSize(1000);
    assertThat(builder.toCursor().getCount()).isEqualTo(1000);
    assertThat(builder.toObservable().toList().toBlocking().first().size()).isEqualTo(1000);
  }

  public void testWhere() {
    final QueryBuilder<SimpleObject> selectStatement =
        shillelagh.selectFrom(SimpleObject.class).where("id").isEqualTo(14);

    assertThat(selectStatement.toString()).isEqualTo("SELECT * FROM SimpleObject WHERE id = 14");
    assertThat(selectStatement.toList()).hasSize(1);
  }

  public void testWhereNotNull() {
    final QueryBuilder<SimpleObject> selectStatement =
        shillelagh.selectFrom(SimpleObject.class).where("id").isNotNull();

    assertThat(selectStatement.toList()).hasSize(1000);
  }

  public void testWhereNull() {
    final QueryBuilder<SimpleObject> selectStatement =
        shillelagh.selectFrom(SimpleObject.class).where("id").isNull();

    assertThat(selectStatement.toList()).hasSize(0);
  }

  public void testWhereNotEqual() {
    final QueryBuilder<SimpleObject> selectStatement =
        shillelagh.selectFrom(SimpleObject.class).where("name").isNotEqualTo(100);

    assertThat(selectStatement.toList()).hasSize(999);
  }

  public void testWhereGreaterThanEqualTo() {
    final QueryBuilder<SimpleObject> selectStatement =
        shillelagh.selectFrom(SimpleObject.class).where("id").isGreaterThanOrEqualTo(999);

    assertThat(selectStatement.toList()).hasSize(2);
  }

  public void testWhereGreaterThan() {
    final QueryBuilder<SimpleObject> selectStatement =
        shillelagh.selectFrom(SimpleObject.class).where("id").isGreaterThan(999);

    assertThat(selectStatement.toList()).hasSize(1);
  }

  public void testWhereLessThanEqualTo() {
    final QueryBuilder<SimpleObject> selectStatement =
        shillelagh.selectFrom(SimpleObject.class).where("id").isLessThanOrEqualTo(2);

    assertThat(selectStatement.toList()).hasSize(2);
  }

  public void testWhereLessThan() {
    final QueryBuilder<SimpleObject> selectStatement =
        shillelagh.selectFrom(SimpleObject.class).where("id").isLessThan(2);

    assertThat(selectStatement.toList()).hasSize(1);
  }

  public void testBetween() {
    final QueryBuilder<SimpleObject> selectStatement =
        shillelagh.selectFrom(SimpleObject.class).where("id").between(10, 12);

    assertThat(selectStatement.toList()).hasSize(3);
  }

  public void testAnd() {
    final QueryBuilder<SimpleObject> selectStatement = shillelagh.selectFrom(SimpleObject.class)
        .where("name")
        .isEqualTo(0)
        .and("address")
        .isEqualTo(1);

    assertThat(selectStatement.toList()).hasSize(1);
  }

  public void testOr() {
    final QueryBuilder<SimpleObject> selectStatement = shillelagh.selectFrom(SimpleObject.class)
        .where("name")
        .isEqualTo(0)
        .or("address")
        .isEqualTo(1);

    assertThat(selectStatement.toList()).hasSize(100);
  }

  public void testLike() {
    final QueryBuilder<SimpleObject> selectStatement =
        shillelagh.selectFrom(SimpleObject.class).where("name").like("%00");

    assertThat(selectStatement.toList()).hasSize(9);
  }

  public void testOrderBy() {
    final List<SimpleObject> objects =
        shillelagh.selectFrom(SimpleObject.class).orderBy("customerId").toList();

    int last = 0;
    for (final SimpleObject simpleObject : objects) {
      assertThat(simpleObject.getCustomerId()).isGreaterThanOrEqualTo(last);
      last = (int) simpleObject.getCustomerId();
    }
  }

  public void testOrderByAscending() {
    final List<SimpleObject> objects =
        shillelagh.selectFrom(SimpleObject.class).orderBy("customerId").ascending().toList();

    int last = 0;
    for (final SimpleObject simpleObject : objects) {
      assertThat(simpleObject.getCustomerId()).isGreaterThanOrEqualTo(last);
      last = (int) simpleObject.getCustomerId();
    }
  }

  public void testOrderByDescending() {
    final List<SimpleObject> objects =
        shillelagh.selectFrom(SimpleObject.class).orderBy("customerId").descending().toList();

    int last = Integer.MAX_VALUE;
    for (final SimpleObject simpleObject : objects) {
      assertThat(simpleObject.getCustomerId()).isLessThanOrEqualTo(last);
      last = (int) simpleObject.getCustomerId();
    }
  }

  private static class QueryBuilderTestSQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "query_builder.db";
    private static final int DATABASE_VERSION = 3;

    public QueryBuilderTestSQLiteOpenHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
      Shillelagh.createTable(db, SimpleObject.class);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Shillelagh.dropTable(db, SimpleObject.class);
      onCreate(db);
    }
  }
}

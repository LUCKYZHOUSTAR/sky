package lucky.sky.db.jsd;

import lombok.Getter;
import lombok.Setter;
import lucky.sky.db.jsd.clause.DeleteEndClause;
import lucky.sky.db.jsd.clause.InsertEndClause;
import lucky.sky.db.jsd.clause.SelectEndClause;
import lucky.sky.db.jsd.clause.UpdateEndClause;
import lucky.sky.db.jsd.result.*;
import lucky.sky.db.jsd.template.SqlTemplateManager;
import lucky.sky.db.jsd.annotation.JsdTable;
import lucky.sky.db.jsd.template.SqlTemplate;
import lucky.sky.util.config.ConfigManager;
import lucky.sky.util.lang.EnumValueSupport;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;


public class MysqlTest {

  private Database db;

  @Before
  public void init() {
    ConfigManager.setConfigDir("../sky-util/src/main/resources/etc/");
    db = DatabaseFactory.open("skygroup");
  }

  @Test
  public void testShardTable() {
    Person person = new Person(2, "test");
    // 方式1
    TableQuery query = db.table("person", person.getId());
    // 方式2
//        TableQuery query = db.table(person);
    BuildResult result;

    //测试
    result = query.insert(person).print();
    println(result);

    result = query.delete(Shortcut.f("id", person.getId())).print();
    println(result);
    result = query.delete(person).print();
    println(result);

    result = query.update(person).print();
    println(result);
    result = query.update(person, "name").print();
    println(result);
    result = query.update(Shortcut.uv("name", "test")).print();
    println(result);

    result = query.select("id", "name").where(Shortcut.f("id", person.getId())).print();
    println(result);
    result = query.select(Person.class).where(Shortcut.f("id", person.getId())).print();
    println(result);
    result = query.select(Shortcut.c("count(*)", "count")).print();
    println(result);
  }

  @Test
  public void testInsert() {
    InsertEndClause clause;
    InsertResult ir;

    // 先删除要创建的记录
    SimpleResult sr = db.delete("person").where(Shortcut.f("id", FilterType.IN, new int[]{1, 2}))
        .result();
    println(String.format("{affectedRows: %d}", sr.getAffectedRows()));

    // 指定主键
    clause = db.insert("person").columns("id", "name").values(1, "n1").values(2, "n2");
    BuildResult br = clause.print();
    println(br);
    ir = clause.result();
    println(String.format("{affectedRows: %d, keys: %s}", ir.getAffectedRows(), ir.getKeys()));

    // 插入对象
    clause = db.insert(new Person(3, "n3"));
    br = clause.print();
    println(br);
    ir = clause.result(true);
    println(String.format("{affectedRows: %d, keys: %s}", ir.getAffectedRows(), ir.getKeys()));

    // 批量插入对象
    ArrayList<Person> persons = new ArrayList<>();
    persons.add(new Person(4, "n4"));
    persons.add(new Person(5, "n5"));
    clause = db.insert(persons);
    br = clause.print();
    println(br);
    ir = clause.result(true);
    println(String.format("{affectedRows: %d, keys: %s}", ir.getAffectedRows(), ir.getKeys()));

    // 自增主键
//        br = db.insert("person").values(1, "n1").values(2, "n2").print();
//        println(br);
//
//        // 返回自增主键值
//        ir = db.insert("person").columns("name").values("n3").values("n4").result(true);
//        if (ir != null) {
//            println(String.format("{affectedRows: %d, keys: %s}", ir.getAffectedRows(), ir.getKeys()));
//        }
  }

  @Test
  public void testUpdate() {
    UpdateValues values = Shortcut.uv("name", "new name");
    Filter f = Shortcut.f("id", 1).or(Shortcut.f("id", 2));   // 更新条件: id = 1 OR id = 2
    UpdateEndClause clause = db.update("person").set(values).where(f);
    BuildResult br = clause.print();
    println(br);
    SimpleResult sr = clause.result();
    println(String.format("{affectedRows: %d}", sr.getAffectedRows()));

    Person p = new Person(22, "abc");
    sr = db.update(p, "name").result();
    println(String.format("{affectedRows: %d}", sr.getAffectedRows()));
  }

  @Test
  public void testDelete() {
    DeleteEndClause clause = db.delete("person").where(Shortcut.f("id", 1));
    BuildResult br = clause.print();
    println(br);

    br = db.delete(new Person(1, "test")).print();
    println(br);

    SimpleResult sr = clause.result();
    println(String.format("{affectedRows: %d}", sr.getAffectedRows()));
  }

  @Test
  public void testSelect() {
    SelectEndClause clause = db.select("Id", "Name", "Age").from("person")
        .where(Shortcut.f("id", 22));

    // value
    Object value = clause.result().value();
    println(value);

    // one
    SelectResult sr = clause.result();
    println(sr.one());

    // one
    sr = clause.result();
    Person p = sr.one(Person.class);
    if (p != null) {
      println(String.format("{id: %d, name: %s}", p.getId(), p.getName()));
    }

    // each
    clause = db.select("id", "name").from("person")
        .where(Shortcut.f("id", FilterType.IN, new int[]{19, 20}));
    sr = clause.result();
    sr.each(reader -> println("each: " + reader.getInt(1)));

    // all
    sr = clause.result();
    List<Person> persons = sr.all(Person.class);
    println("all: " + persons.size());

    // select by class
    p = db.select(Person.class).where(Shortcut.f("id", 22)).result().one(Person.class);
    if (p != null) {
      println(String.format("{id: %d, name: %s, age: %d, status: %s, time: %s}",
          p.getId(), p.getName(), p.getAge(), p.getStatus(), p.getTime()));
    }

    // select by object
    p = db.select(new Person(22, null)).result().one(Person.class);
    println(p);
  }

  @Test
  public void testSelect1() throws Exception {
    Table t1 = Shortcut.t("Activity", "A");
    Table t2 = Shortcut.t("ActivityCategory", "AC");
    Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2014-11-20");
    SelectEndClause clause = db.select(Shortcut.c(t2, "ID", "NAME_CN").add("COUNT(*)", "Count"))
        .from(t1).join(t2, Shortcut.f(t1, "ActivityCategory", t2, "ID"))
        .where(Shortcut.f(t1, "ENTER_TIME", FilterType.LT, date))
        .groupBy(Shortcut.g(t2, "ID", "NAME_CN"))
        .having(Shortcut.f("Count", FilterType.GT, 0))
        .orderBy(Shortcut.s(SortType.ASC, t1, "ActivityCategory"))
        .limit(0, 10);
    println(clause.print());
  }

  @Test
  public void testTransaction() throws Exception {
    // 方式1
    db.begin(tx -> {
      SimpleResult sr = tx.update("person").set(Shortcut.uv("name", "yyy"))
          .where(Shortcut.f("id", 19)).result();
      println(sr.getAffectedRows());

      Map<String, Object> map = tx.select("id", "name").from("person").where(Shortcut.f("id", 19))
          .result().one();
      println(map);
    });

    // 方式2
    try (Transaction tx = db.begin()) {
      SimpleResult sr = tx.update("person").set(Shortcut.uv("name", "yyy"))
          .where(Shortcut.f("id", 19)).result();
      println(sr.getAffectedRows());

      Map<String, Object> map = tx.select("id", "name").from("person").where(Shortcut.f("id", 19))
          .result().one();
      println(map);

      tx.commit();
    }

    // 上下文事务
    db.begin(tx -> {
      SimpleResult sr = tx.update("person").set(Shortcut.uv("name", "yyy"))
          .where(Shortcut.f("id", 19)).result();
      println(sr.getAffectedRows());

      useContextTransaction();
    }, true);
  }

  private void useContextTransaction() {
    Transaction tx = Transaction.get();
    Map<String, Object> map = tx.select("id", "name").from("person").where(Shortcut.f("id", 19))
        .result().one();
    println(map);
  }

  @Test
  public void testExecute() {
    try (ExecuteResult er = db.execute("select * from person where id<?", 100).result()) {
      Person p = er.one(Person.class);
      if (p != null) {
        println(String.format("{id: %d, name: %s}", p.getId(), p.getName()));
      }

      List<Person> ps = er.all(Person.class);
      if (ps != null) {
        println(String.format("total persons: %d", ps.size()));
      }
    }

    try (ExecuteResult er = db.execute("update person set age = 23 where id=?", 23).result()) {
      int affectedRows = er.getAffectedRows();
      println(String.format("affected rows: %d", affectedRows));
    }
  }

  @Test
  public void testExecuteTemplate() {
    SqlTemplate sqlTemplate = SqlTemplateManager.get("getPerson");

    // map 参数
    Map<String, Object> param = new HashMap<>();
    param.put("age", 23);
    param.put("name", "n3");
    param.put("id", 23);
    ExecuteResult er = db.execute(sqlTemplate, param).result();
    Person p = er.one(Person.class);
    if (p != null) {
      println(String.format("{id: %d, name: %s}", p.getId(), p.getName()));
    }

    // 实体参数
    p = new Person(23, "n3");
    p.setAge(23);
    er = db.execute(sqlTemplate, p).result();
    p = er.one(Person.class);
    if (p != null) {
      println(String.format("{id: %d, name: %s}", p.getId(), p.getName()));
    }
  }

  @Test
  public void testCall() {
//        db.call("GetUser").with(null).result();
  }

  @Test
  public void testPrepare() {
//        db.call("GetUser").with(null).result();
  }

  @Test
  public void TestMap() {
//        ArrayList<Integer> list = new ArrayList<>();
//        list.add(1);
//        Type[] types = list.getClass().getGenericInterfaces();
//        println(list.getClass() == List.class);
  }

  private void println(Object obj) {
    System.out.println(obj);
  }

  /**
   * 测试用
   */
  @Getter
  @Setter
  @JsdTable(nameStyle = NameStyle.LOWER, shardKeys = {"id"})
  public static class Person {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    private int age;
    private PersonStatus status;
    private LocalDateTime time;

    public Person() {
    }

    public Person(int id, String name) {
      this.id = id;
      this.name = name;
    }
  }

  public enum PersonStatus implements EnumValueSupport {
    VALID(1), INVALID(2);

    private int value;

    PersonStatus(int value) {
      this.value = value;
    }

    @Override
    public int value() {
      return value;
    }
  }
}

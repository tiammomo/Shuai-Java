package com.shuai.database.mongodb;

/**
 * Spring Data MongoDB 演示类
 *
 * 核心内容
 * ----------
 *   - 实体注解
 *   - Repository 接口
 *   - MongoTemplate 操作
 *
 * @author Shuai
 * @version 1.0
 */
public class SpringDataMongoDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       Spring Data MongoDB");
        System.out.println("=".repeat(50));

        entityAnnotations();
        repositoryInterface();
        mongoTemplate();
    }

    /**
     * 实体注解
     */
    private void entityAnnotations() {
        System.out.println("\n--- 实体注解 ---");

        System.out.println("  @Document:");
        System.out.println("    - 标记为文档实体");
        System.out.println("    - collection 属性指定集合名");
        System.out.println("    @Document(collection = \"users\")");

        System.out.println("\n  @Id:");
        System.out.println("    - 主键字段");
        System.out.println("    - 支持 String、ObjectId 等类型");

        System.out.println("\n  @Field:");
        System.out.println("    - 指定字段名（与属性名不同时使用）");
        System.out.println("    @Field(\"user_name\")");

        System.out.println("\n  @Indexed:");
        System.out.println("    - 创建索引");
        System.out.println("    @Indexed(unique = true)");
        System.out.println("    @Indexed(sparse = true)");

        System.out.println("\n  @DBRef:");
        System.out.println("    - 关联其他文档");
        System.out.println("    @DBRef private List<Order> orders;");

        System.out.println("\n  实体示例:");
        System.out.println("    @Document(collection = \"users\")");
        System.out.println("    public class User {");
        System.out.println("        @Id");
        System.out.println("        private String id;");
        System.out.println("        @Indexed(unique = true)");
        System.out.println("        private String email;");
        System.out.println("        private String name;");
        System.out.println("        private Integer age;");
        System.out.println("        private List<String> skills;");
        System.out.println("    }");
    }

    /**
     * Repository 接口
     */
    private void repositoryInterface() {
        System.out.println("\n--- Repository 接口 ---");

        System.out.println("  基本 Repository:");
        System.out.println("    public interface UserRepository extends MongoRepository<User, String> { }");

        System.out.println("\n  方法命名查询:");
        System.out.println("    Optional<User> findByEmail(String email);");
        System.out.println("    List<User> findByAgeGreaterThan(Integer age);");
        System.out.println("    List<User> findByStatusAndAge(String status, Integer age);");
        System.out.println("    List<User> findBySkillsContaining(String skill);");
        System.out.println("    long countByStatus(String status);");
        System.out.println("    boolean existsByEmail(String email);");

        System.out.println("\n  模糊查询:");
        System.out.println("    @Query(\"{ 'name': { $regex: ?0 } }\")");
        System.out.println("    List<User> findByNameStartingWith(String prefix);");

        System.out.println("\n  复杂查询:");
        System.out.println("    @Query(\"{ 'age': { $gte: ?0, $lte: ?1 }, 'status': ?2 }\")");
        System.out.println("    List<User> findByAgeRange(Integer minAge, Integer maxAge, String status);");

        System.out.println("\n  分页查询:");
        System.out.println("    Page<User> findByStatus(String status, Pageable pageable);");
        System.out.println("    Sort sort = Sort.by(\"createdAt\").descending();");
        System.out.println("    Pageable pageable = PageRequest.of(0, 10, sort);");
    }

    /**
     * MongoTemplate 操作
     */
    private void mongoTemplate() {
        System.out.println("\n--- MongoTemplate 操作 ---");

        System.out.println("  保存:");
        System.out.println("    mongoTemplate.save(user)  // 保存或更新");
        System.out.println("    mongoTemplate.insert(user)  // 插入（ID 存在会报错）");

        System.out.println("\n  查询:");
        System.out.println("    mongoTemplate.findById(id, User.class)");
        System.out.println("    mongoTemplate.findOne(query, User.class)");
        System.out.println("    mongoTemplate.find(query, User.class)  // 返回列表");
        System.out.println("    mongoTemplate.exists(query, User.class)");

        System.out.println("\n  条件查询:");
        System.out.println("    Query query = new Query();");
        System.out.println("    query.addCriteria(Criteria.where(\"status\").is(\"active\"));");
        System.out.println("    query.addCriteria(Criteria.where(\"age\").gte(18));");
        System.out.println("    query.with(Sort.by(\"createdAt\").descending());");
        System.out.println("    query.skip(20).limit(10);");
        System.out.println("    List<User> users = mongoTemplate.find(query, User.class);");

        System.out.println("\n  更新:");
        System.out.println("    Update update = new Update();");
        System.out.println("    update.set(\"status\", \"active\");");
        System.out.println("    update.inc(\"age\", 1);");
        System.out.println("    update.push(\"skills\", \"NewSkill\");");
        System.out.println("    UpdateResult result = mongoTemplate.updateFirst(query, update, User.class);");

        System.out.println("\n  删除:");
        System.out.println("    mongoTemplate.remove(query, User.class)");
        System.out.println("    mongoTemplate.findAndRemove(query, User.class)");
    }
}

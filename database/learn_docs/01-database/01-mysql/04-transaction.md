# 事务与并发控制

## 事务基础

### ACID 特性

| 特性 | 说明 | 保障机制 |
|------|------|----------|
| Atomicity（原子性） | 事务要么全做，要么全不做 | undo log |
| Consistency（一致性） | 事务前后数据一致 | 约束、触发器 |
| Isolation（隔离性） | 并发事务互不干扰 | 锁、MVCC |
| Durability（持久性） | 事务提交后永久保存 | redo log |

### 事务控制语句

```sql
-- 开启事务
START TRANSACTION;
-- 或
BEGIN;

-- 提交事务
COMMIT;

-- 回滚事务
ROLLBACK;

-- 设置保存点
SAVEPOINT sp1;

-- 回滚到保存点
ROLLBACK TO sp1;

-- 设置事务隔离级别
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
```

### 自动提交

```sql
-- 查看自动提交状态
SELECT @@autocommit;

-- 关闭自动提交
SET autocommit = 0;

-- 开启自动提交
SET autocommit = 1;
```

## 隔离级别

### 隔离级别定义

| 隔离级别 | 脏读 | 不可重复读 | 幻读 |
|----------|------|------------|------|
| READ UNCOMMITTED | 可能 | 可能 | 可能 |
| READ COMMITTED | 不可能 | 可能 | 可能 |
| REPEATABLE READ | 不可能 | 不可能 | 可能（InnoDB 解决了） |
| SERIALIZABLE | 不可能 | 不可能 | 不可能 |

### 设置隔离级别

```sql
-- 会话级别（当前连接）
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;

-- 全局级别（所有连接）
SET GLOBAL TRANSACTION ISOLATION LEVEL READ COMMITTED;

-- 查看当前隔离级别
SELECT @@transaction_isolation;
SELECT @@tx_isolation;
```

### 各隔离级别演示

```sql
-- READ COMMITTED
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN;
-- 会话1：读取（看到其他已提交的事务）
SELECT balance FROM accounts WHERE id = 1;
COMMIT;

-- REPEATABLE READ（MySQL 默认）
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
BEGIN;
-- 会话1：多次读取结果一致
SELECT balance FROM accounts WHERE id = 1;
-- 会话2：修改并提交
UPDATE accounts SET balance = balance - 100 WHERE id = 1;
COMMIT;
-- 会话1：再次读取，结果与第一次相同
SELECT balance FROM accounts WHERE id = 1;
COMMIT;
```

## 锁机制

### 锁类型

| 锁类型 | 说明 | 作用范围 |
|--------|------|----------|
| 共享锁 (S) | 读锁，多个事务可同时持有 | 行/表 |
| 排他锁 (X) | 写锁，独占 | 行/表 |
| 意向锁 | 表锁，表示有行锁 | 表 |

### 行锁

```sql
-- 共享锁
SELECT * FROM users WHERE id = 1 LOCK IN SHARE MODE;

-- 排他锁
SELECT * FROM users WHERE id = 1 FOR UPDATE;
```

### 锁等待与死锁

```sql
-- 查看锁状态
SHOW ENGINE INNODB STATUS;

-- 查看当前锁
SELECT * FROM information_schema.INNODB_LOCKS;
SELECT * FROM information_schema.INNODB_LOCK_WAITS;

-- 查看当前事务
SELECT * FROM information_schema.INNODB_TRX;
```

### 死锁示例

```sql
-- 会话1
BEGIN;
UPDATE users SET balance = balance - 100 WHERE id = 1;
-- 等待...（被会话2阻塞）
UPDATE users SET balance = balance + 100 WHERE id = 2;

-- 会话2
BEGIN;
UPDATE users SET balance = balance - 100 WHERE id = 2;
UPDATE users SET balance = balance + 100 WHERE id = 1;
-- 死锁！
```

## Java 事务操作

### 编程式事务

```java
// 方式1: 使用 Connection
conn.setAutoCommit(false);
try {
    // 操作1
    PreparedStatement stmt1 = conn.prepareStatement("UPDATE accounts SET balance = balance - 100 WHERE id = ?");
    stmt1.setLong(1, 1);
    stmt1.executeUpdate();

    // 操作2
    PreparedStatement stmt2 = conn.prepareStatement("UPDATE accounts SET balance = balance + 100 WHERE id = ?");
    stmt2.setLong(1, 2);
    stmt2.executeUpdate();

    conn.commit();
} catch (Exception e) {
    conn.rollback();
    throw e;
} finally {
    conn.setAutoCommit(true);
}

// 方式2: 使用事务管理器
@Autowired
private TransactionTemplate transactionTemplate;

public void transfer(Long fromId, Long toId, BigDecimal amount) {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
        @Override
        protected void doInTransactionWithoutResult(TransactionStatus status) {
            // 操作1
            accountDao.withdraw(fromId, amount);
            // 操作2
            accountDao.deposit(toId, amount);
        }
    });
}
```

### 隔离级别设置

```java
// 设置隔离级别
conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

// 查看隔离级别
int level = conn.getTransactionIsolation();
```

### Savepoint

```java
conn.setAutoCommit(false);
try {
    // 操作1
    stmt.executeUpdate("UPDATE accounts SET balance = balance - 100 WHERE id = 1");

    // 创建保存点
    Savepoint sp = conn.setSavepoint("sp1");

    // 操作2
    stmt.executeUpdate("UPDATE accounts SET balance = balance + 100 WHERE id = 2");

    // 回滚到保存点
    conn.rollback(sp);

    conn.commit();
} catch (Exception e) {
    conn.rollback();
}
```

## 分布式事务

### 两阶段提交（2PC）

```java
// 伪代码示例
public boolean transfer(Long fromId, Long toId, BigDecimal amount) {
    // 阶段1: 准备
    boolean fromPrepared = prepare(fromId, amount);
    boolean toPrepared = prepare(toId, amount);

    // 阶段2: 提交
    if (fromPrepared && toPrepared) {
        commit(fromId);
        commit(toId);
        return true;
    } else {
        rollback(fromId);
        rollback(toId);
        return false;
    }
}
```

### TCC 模式

```java
// Try: 预留资源
public void tryDeduct(Long accountId, BigDecimal amount) {
    accountDao.updateBalance(accountId, amount.negate());

// Confirm: 确认扣款
public void confirmDeduct(Long accountId, BigDecimal amount) {
    // 确认扣款，记录日志
    accountDao.insertDeductLog(accountId, amount);
}

// Cancel: 取消扣款
public void cancelDeduct(Long accountId, BigDecimal amount) {
    accountDao.updateBalance(accountId, amount);
}
```

## 事务最佳实践

1. **控制事务范围**
   ```java
   // 不好：事务范围过大
   @Transactional
   public void process() {
       query1();  // 只读操作
       query2();  // 只读操作
       update();  // 写操作
   }

   // 好：只对写操作加事务
   @Transactional
   public void update() {
       update();
   }

   @Transactional(readOnly = true)
   public void query() {
       query();
   }
   ```

2. **避免长事务**
   ```java
   // 不好：长事务
   @Transactional
   public void process() {
       for (Item item : items) {
           processItem(item);  // 耗时操作
       }
   }

   // 好：分批处理
   @Transactional
   public void processBatch(List<Item> items, int batchSize) {
       for (int i = 0; i < items.size(); i += batchSize) {
           List<Item> batch = items.subList(i, Math.min(i + batchSize, items.size()));
           processBatchItems(batch);
       }
   }
   ```

3. **合理使用锁**
   ```java
   // 使用乐观锁
   @Version
   private Long version;

   // 使用悲观锁（必要时）
   @Query("SELECT a FROM Account a WHERE a.id = :id")
   Account findByIdWithLock(@Param("id") Long id);

   // SQL 中使用 FOR UPDATE
   SELECT * FROM accounts WHERE id = ? FOR UPDATE;
   ```

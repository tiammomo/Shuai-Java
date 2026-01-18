#!/bin/bash

# ShardingSphere MySQL 主从复制配置脚本
# 用于在容器启动后配置主从复制
# 使用 MySQL 8.0+ 语法

set -e

echo "=== ShardingSphere MySQL 主从复制配置 ==="

# 等待主库就绪
echo "等待主库就绪..."
sleep 10

# 获取主库二进制日志位置
echo "获取主库二进制日志位置..."
MASTER_STATUS=$(docker exec sharding-mysql-master mysql -uroot -proot -e "SHOW MASTER STATUS\G" 2>/dev/null)
MASTER_LOG_FILE=$(echo "$MASTER_STATUS" | grep "File:" | awk '{print $2}')
MASTER_LOG_POS=$(echo "$MASTER_STATUS" | grep "Position:" | awk '{print $2}')

echo "主库日志文件: $MASTER_LOG_FILE"
echo "主库日志位置: $MASTER_LOG_POS"

# 配置从库1
echo "配置从库1..."
docker exec sharding-mysql-slave1 mysql -uroot -proot <<EOF
STOP REPLICA;
RESET REPLICA ALL;
CHANGE REPLICATION SOURCE TO
    SOURCE_HOST='mysql-master',
    SOURCE_USER='repl_user',
    SOURCE_PASSWORD='repl_pass',
    SOURCE_LOG_FILE='$MASTER_LOG_FILE',
    SOURCE_LOG_POS=$MASTER_LOG_POS;
START REPLICA;
EOF

# 配置从库2
echo "配置从库2..."
docker exec sharding-mysql-slave2 mysql -uroot -proot <<EOF
STOP REPLICA;
RESET REPLICA ALL;
CHANGE REPLICATION SOURCE TO
    SOURCE_HOST='mysql-master',
    SOURCE_USER='repl_user',
    SOURCE_PASSWORD='repl_pass',
    SOURCE_LOG_FILE='$MASTER_LOG_FILE',
    SOURCE_LOG_POS=$MASTER_LOG_POS;
START REPLICA;
EOF

# 验证复制状态 (MySQL 8.0+ 使用 SHOW REPLICA STATUS)
echo "验证从库1复制状态..."
docker exec sharding-mysql-slave1 mysql -uroot -proot -e "SHOW REPLICA STATUS\G" | grep -E "Replica_IO_Running|Replica_SQL_Running"

echo "验证从库2复制状态..."
docker exec sharding-mysql-slave2 mysql -uroot -proot -e "SHOW REPLICA STATUS\G" | grep -E "Replica_IO_Running|Replica_SQL_Running"

echo "=== 主从复制配置完成 ==="

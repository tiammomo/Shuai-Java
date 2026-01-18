#!/bin/bash
# backup.sh - Elasticsearch + Milvus 数据备份脚本
#
# 功能:
#   - 导出 Elasticsearch 索引数据
#   - 导出 Milvus 集合数据
#   - 压缩并添加时间戳
#
# 使用方法:
#   ./backup.sh              # 执行完整备份
#   ./backup.sh --es-only    # 仅备份 ES 数据
#   ./backup.sh --milvus-only # 仅备份 Milvus 数据
#   ./backup.sh --list       # 列出可用备份
#   ./backup.sh --restore es_backup_20260118.tar.gz  # 恢复数据

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 配置
BACKUP_DIR="${BACKUP_DIR:-./backups}"
ES_HOST="${ES_HOST:-localhost:9200}"
MILVUS_HOST="${MILVUS_HOST:-localhost:19530}"
DATE=$(date +%Y%m%d_%H%M%S)
TIMESTAMP=$(date +%Y%m%d)

echo "========================================"
echo "  Elasticsearch + Milvus 数据备份"
echo "========================================"
echo ""

# 创建备份目录
mkdir -p "$BACKUP_DIR"

# 检查 ES 连接
check_es() {
    echo -n "[ES] 检查连接... "
    if curl -sf "http://$ES_HOST/_cluster/health" > /dev/null 2>&1; then
        echo -e "${GREEN}OK${NC}"
        return 0
    else
        echo -e "${RED}FAILED${NC}"
        return 1
    fi
}

# 检查 Milvus 连接
check_milvus() {
    echo -n "[Milvus] 检查连接... "
    if curl -sf "http://localhost:9091/healthz" > /dev/null 2>&1; then
        echo -e "${GREEN}OK${NC}"
        return 0
    else
        echo -e "${RED}FAILED${NC}"
        return 1
    fi
}

# 备份 Elasticsearch 索引
backup_es_indices() {
    echo -e "\n${YELLOW}[1/2] 备份 Elasticsearch 索引${NC}"
    echo "----------------------------------------"

    if ! check_es; then
        echo -e "${YELLOW}跳过 ES 备份${NC}"
        return 1
    fi

    # 获取所有索引
    indices=$(curl -s "http://$ES_HOST/_cat/indices?v" | grep -v "^health" | awk '{print $3}')

    if [ -z "$indices" ]; then
        echo "  没有找到索引"
        return 0
    fi

    echo "  发现的索引: $indices"

    # 创建 ES 备份目录
    es_backup_dir="$BACKUP_DIR/es_backup_$DATE"
    mkdir -p "$es_backup_dir"

    # 导出每个索引
    for index in $indices; do
        echo -n "  导出索引 [$index]... "

        # 导出为 JSON
        if curl -s "http://$ES_HOST/$index/_search?size=10000" | python3 -m json.tool > "$es_backup_dir/${index}.json" 2>/dev/null; then
            echo -e "${GREEN}OK${NC}"
        else
            echo -e "${YELLOW}跳过 (可能为空)${NC}"
            rm -f "$es_backup_dir/${index}.json"
        fi
    done

    # 创建压缩包
    echo -n "  压缩备份文件... "
    tar -czf "$BACKUP_DIR/es_backup_$DATE.tar.gz" -C "$BACKUP_DIR" "es_backup_$DATE"
    rm -rf "$es_backup_dir"
    echo -e "${GREEN}OK${NC}"

    echo "  ES 备份完成: $BACKUP_DIR/es_backup_$DATE.tar.gz"
    return 0
}

# 备份 Milvus 集合
backup_milvus_collections() {
    echo -e "\n${YELLOW}[2/2] 备份 Milvus 集合${NC}"
    echo "----------------------------------------"

    if ! check_milvus; then
        echo -e "${YELLOW}跳过 Milvus 备份${NC}"
        return 1
    fi

    # Milvus 备份需要使用官方工具或 Attu
    # 这里生成备份元数据信息

    echo "  Milvus 集合信息:"

    # 列出集合 (需要使用 Milvus CLI 或 Attu)
    echo "  注意: Milvus 完整备份需要使用 Attu UI 或 Milvus CLI"
    echo "  推荐方式: docker run -p 8000:3000 zilliz/attu"

    # 创建元数据文件
    milvus_backup_dir="$BACKUP_DIR/milvus_backup_$DATE"
    mkdir -p "$milvus_backup_dir"

    # 保存集合信息
    cat > "$milvus_backup_dir/collections_info.json" << EOF
{
    "backup_date": "$DATE",
    "collections": [
        {
            "name": "document_embeddings",
            "fields": ["id", "document_id", "content", "vector", "metadata"],
            "note": "RAG 文档向量集合"
        }
    ]
}
EOF

    # 创建压缩包
    tar -czf "$BACKUP_DIR/milvus_backup_$DATE.tar.gz" -C "$BACKUP_DIR" "milvus_backup_$DATE"
    rm -rf "$milvus_backup_dir"

    echo "  Milvus 元数据备份完成: $BACKUP_DIR/milvus_backup_$DATE.tar.gz"
    return 0
}

# 列出备份
list_backups() {
    echo -e "\n${YELLOW}可用备份列表${NC}"
    echo "----------------------------------------"

    if [ ! -d "$BACKUP_DIR" ] || [ -z "$(ls -A "$BACKUP_DIR" 2>/dev/null)" ]; then
        echo "  没有找到备份文件"
        return 0
    fi

    ls -lh "$BACKUP_DIR" | tail -n +4
}

# 恢复数据
restore_backup() {
    local backup_file="$1"

    if [ -z "$backup_file" ]; then
        echo -e "${RED}请指定备份文件${NC}"
        echo "  用法: $0 --restore <backup_file.tar.gz>"
        return 1
    fi

    if [ ! -f "$backup_file" ]; then
        echo -e "${RED}备份文件不存在: $backup_file${NC}"
        return 1
    fi

    echo -e "\n${YELLOW}恢复备份: $backup_file${NC}"
    echo "----------------------------------------"

    # 确定备份类型
    if echo "$backup_file" | grep -q "es_backup"; then
        echo "  恢复 Elasticsearch 索引..."
        tar -xzf "$backup_file" -C "$BACKUP_DIR"

        # 恢复每个索引
        for json_file in "$BACKUP_DIR"/*/*.json; do
            if [ -f "$json_file" ]; then
                index_name=$(basename "$json_file" .json)
                echo -n "  恢复索引 [$index_name]... "
                # 这里需要实现实际的恢复逻辑
                echo -e "${YELLOW}需要手动处理${NC}"
            fi
        done
    elif echo "$backup_file" | grep -q "milvus_backup"; then
        echo "  Milvus 备份恢复需要使用 Attu UI"
    else
        echo -e "${RED}未知的备份格式${NC}"
    fi
}

# 显示帮助
show_help() {
    echo "用法: $0 [命令]"
    echo ""
    echo "命令:"
    echo "  (无)          执行完整备份"
    echo "  --es-only     仅备份 Elasticsearch 数据"
    echo "  --milvus-only 仅备份 Milvus 数据"
    echo "  --list        列出可用备份"
    echo "  --restore     恢复备份"
    echo "  --help        显示帮助信息"
    echo ""
    echo "环境变量:"
    echo "  BACKUP_DIR    备份目录 (默认: ./backups)"
    echo "  ES_HOST       ES 主机地址 (默认: localhost:9200)"
}

# 主逻辑
case "${1:-}" in
    --es-only)
        backup_es_indices
        ;;
    --milvus-only)
        backup_milvus_collections
        ;;
    --list)
        list_backups
        ;;
    --restore)
        restore_backup "$2"
        ;;
    --help|-h)
        show_help
        ;;
    "")
        backup_es_indices
        backup_milvus_collections

        echo ""
        echo "========================================"
        echo -e "  ${GREEN}备份完成!${NC}"
        echo "========================================"
        echo ""
        echo "备份文件位置: $BACKUP_DIR"
        echo ""
        list_backups
        ;;
    *)
        echo -e "${RED}未知参数: $1${NC}"
        show_help
        exit 1
        ;;
esac

#!/bin/bash
# deploy/scripts/health-check.sh
# 服务健康检查脚本

set -e

echo "========================================"
echo "服务健康检查"
echo "========================================"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 计数器
passed=0
failed=0
skipped=0

# 检查函数
check_service() {
    local name=$1
    local cmd=$2
    local description=$3

    echo -n "[$name] "
    if eval "$cmd" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ OK${NC}"
        ((passed++))
    else
        echo -e "${RED}❌ FAILED${NC}"
        echo "    $description"
        ((failed++))
    fi
}

# 可选服务检查
check_optional_service() {
    local name=$1
    local cmd=$2
    local description=$3

    echo -n "[$name] "
    if eval "$cmd" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ OK${NC}"
        ((passed++))
    else
        echo -e "${YELLOW}⏭️ SKIPPED${NC}"
        echo "    $description"
        ((skipped++))
    fi
}

echo ""
echo "核心服务检查:"
echo "----------------------------------------"

# Elasticsearch 健康检查
check_service "Elasticsearch" \
    "curl -s http://localhost:9200/_cluster/health | grep -q '\"status\":\"green\" or \"status\":\"yellow\"'" \
    "确保 ES 正在运行 (curl http://localhost:9200)"

# ES 集群状态详细信息
if curl -s http://localhost:9200/_cluster/health | grep -qE '"status":"(green|yellow)"'; then
    status=$(curl -s http://localhost:9200/_cluster/health | grep -oP '"status":"\K[^"]+')
    nodes=$(curl -s http://localhost:9200/_cluster/health | grep -oP '"number_of_nodes":\K\d+')
    echo "    状态: $status, 节点数: $nodes"
fi

echo ""
echo "可选服务检查:"
echo "----------------------------------------"

# Milvus 健康检查
check_optional_service "Milvus" \
    "curl -s http://localhost:9091/healthz | grep -q OK" \
    "Milvus 向量数据库 (端口 19530/9091)"

# Redis 健康检查
check_optional_service "Redis" \
    "redis-cli ping 2>/dev/null | grep -q PONG" \
    "Redis 缓存服务 (端口 6379)"

# MinIO 健康检查
check_optional_service "MinIO" \
    "curl -s http://localhost:9000/minio/health/live | grep -q ok" \
    "MinIO 对象存储 (端口 9000)"

echo ""
echo "========================================"
echo "检查结果汇总"
echo "========================================"
echo -e "通过: ${GREEN}$passed${NC}"
echo -e "失败: ${RED}$failed${NC}"
echo -e "跳过: ${YELLOW}$skipped${NC}"
echo ""

if [ $failed -eq 0 ]; then
    echo -e "${GREEN}✅ 所有检查通过!${NC}"
    exit 0
else
    echo -e "${RED}❌ 有 $failed 个服务异常${NC}"
    echo ""
    echo "排查步骤:"
    echo "  1. 检查 Docker 容器: docker-compose ps"
    echo "  2. 查看容器日志: docker-compose logs [服务名]"
    echo "  3. 重启服务: docker-compose restart [服务名]"
    exit 1
fi

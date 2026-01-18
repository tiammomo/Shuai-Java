#!/bin/bash
# deploy/scripts/run-tests.sh
# 一键测试运行脚本

set -e

echo "========================================"
echo "Elasticsearch + Milvus 一键测试"
echo "========================================"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 项目根目录
PROJECT_ROOT="/home/ubuntu/learn_projects/Shuai-Java"
cd "$PROJECT_ROOT"

echo ""
echo "[1/5] 检查服务状态..."
echo "----------------------------------------"
bash elasticsearch/deploy/scripts/health-check.sh

echo ""
echo "[2/5] 编译项目..."
echo "----------------------------------------"
if mvn compile -q -pl elasticsearch; then
    echo -e "${GREEN}✅ 编译成功${NC}"
else
    echo -e "${RED}❌ 编译失败${NC}"
    exit 1
fi

echo ""
echo "[3/5] 运行演示程序..."
echo "----------------------------------------"
# 增加 JVM 内存以支持 RAG 等内存密集型操作
export MAVEN_OPTS="-Xmx2g -XX:MaxMetaspaceSize=512m"
mvn -pl elasticsearch exec:java \
    -Dexec.mainClass=com.shuai.elasticsearch.ElasticsearchDemo \
    2>&1 | tee elasticsearch/test-output.log

echo ""
echo "[4/5] 测试结果分析..."
echo "----------------------------------------"

# 分析测试输出
total_modules=$(grep -c "演示" elasticsearch/test-output.log 2>/dev/null || echo "0")
passed_tests=$(grep -c "✅\|测试完成\|演示执行完成" elasticsearch/test-output.log 2>/dev/null || echo "0")
skipped_tests=$(grep -c "\[跳过\]" elasticsearch/test-output.log 2>/dev/null || echo "0")
errors=$(grep -c "错误\|Exception\|FAILED" elasticsearch/test-output.log 2>/dev/null || echo "0")

echo "测试统计:"
echo "  - 演示模块数: $total_modules"
echo -e "  - 执行成功: ${GREEN}$passed_tests${NC}"
echo -e "  - 跳过模块: ${YELLOW}$skipped_tests${NC}"
echo -e "  - 错误数量: ${RED}$errors${NC}"

echo ""
echo "[5/5] ES 索引验证..."
echo "----------------------------------------"
indices=$(curl -s "localhost:9200/_cat/indices?v" 2>/dev/null || echo "无法连接 ES")
if [ "$indices" != "无法连接 ES" ]; then
    echo "$indices"
else
    echo -e "${YELLOW}无法连接到 Elasticsearch${NC}"
fi

echo ""
echo "========================================"
echo "测试完成!"
echo "========================================"
echo ""
echo "详细输出: elasticsearch/test-output.log"
echo "如需进一步排查, 请查看日志文件"
echo ""

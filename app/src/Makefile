all: java

.PHONY: java

java: main/java/edu/wing/yytang/protocol/SVMPProtocol.java

main/java/edu/wing/yytang/protocol/SVMPProtocol.java: svmp.proto
	protoc --proto_path=. --java_out=./main/java ./svmp.proto


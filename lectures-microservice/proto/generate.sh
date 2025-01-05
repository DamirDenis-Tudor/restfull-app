pip install grpcio-tools
python -m grpc_tools.protoc -I. --python_out=. --pyi_out=.  --grpc_python_out=. auth.proto
sed -i 's/import auth_pb2 as auth__pb2/import proto.auth_pb2 as auth__pb2/g' *.py

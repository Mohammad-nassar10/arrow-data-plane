package org.m4d.adp.allocator;

import org.apache.arrow.memory.AllocationManager;
import org.apache.arrow.memory.ArrowBuf;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.ReferenceManager;


/**
 * A factory class for creating WasmAllocationManager.
 */
public class WasmAllocationFactory implements AllocationManager.Factory, AutoCloseable{
    // Pointer to an instance of the WASM module that is used to allocate and deallocate memory.
    private long instancePtr;

    public WasmAllocationFactory(String wasmImage) {
        System.out.println("wasmallocationfactory" + wasmImage);
        if(wasmImage == null || wasmImage.length() == 0) {
            instancePtr = AllocatorInterface.wasmInstance("ghcr.io/the-mesh-for-data/alloc-transform:v1");
        }
        else {
            instancePtr = AllocatorInterface.wasmInstance(wasmImage);
        }
    }

    public WasmAllocationFactory() {
        instancePtr = AllocatorInterface.wasmInstance("ghcr.io/the-mesh-for-data/alloc-transform:v1");
    }

    @Override
    public AllocationManager create(BufferAllocator accountingAllocator, long size) {
        return new WasmAllocationManager(instancePtr, accountingAllocator, size);
    }

    @Override
    public ArrowBuf empty() {
      long memAddr = AllocatorInterface.wasmMemPtr(instancePtr);
      long offset = AllocatorInterface.wasmAlloc(instancePtr,0);
      return new ArrowBuf(ReferenceManager.NO_OP,
      null,
      0,
      memAddr+offset
      );
    }

    @Override
    public void close() throws Exception {
        AllocatorInterface.wasmDrop(instancePtr);
        
    }

    public long wasmInstancePtr() {
        return instancePtr;
    }
};
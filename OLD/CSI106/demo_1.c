#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define M 307

typedef struct {
    int key;
    char name[50];
    float value;
} Record;

Record* table[M];

// Hash function
int hash_function(int key) {
    return (key % M + 1);
}

// Insert với Linear Probing
void insert(int key, char* name, float value) {
    int address = hash_function(key);
    
    // Tìm ô trống (Linear Probing)
    while (table[address] != NULL) {
        address = (address + 1) % M;
    }
    
    // Tạo record mới
    Record* record = (Record*)malloc(sizeof(Record));
    record->key = key;
    strcpy(record->name, name);
    record->value = value;
    
    table[address] = record;
    printf("Insert key %d -> address %d\n", key, address);
}

// Search với Linear Probing
Record* search(int key) {
    int address = hash_function(key);
    
    while (table[address] != NULL) {
        if (table[address]->key == key) {
            return table[address];
        }
        address = (address + 1) % M;
    }
    return NULL;
}

// Hiển thị bảng
void display_table() {
    printf("\n--- HASH TABLE ---\n");
    for (int i = 0; i < M; i++) {
        if (table[i] != NULL) {
            printf("[%d] Key:%d | %s | %.2f\n", 
                   i, table[i]->key, table[i]->name, table[i]->value);
        }
    }
}

int main() {
    // Khởi tạo
    for (int i = 0; i < M; i++) {
        table[i] = NULL;
    }
    
    // Insert - giống slide
    printf("=== INSERT ===\n");
    insert(123013, "Doc Lee", 1105.45);
    insert(151564, "Rich White", 708.22);
    
    // Hiển thị
    display_table();
    
    // Search
    printf("\n=== SEARCH ===\n");
    Record* r = search(123113);
    if (r != NULL) {
        printf("Found: %s - %.2f\n", r->name, r->value);
    }
    
    return 0;
}
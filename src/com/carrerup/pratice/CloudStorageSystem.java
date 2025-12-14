package com.carrerup.pratice;

import java.util.*;

class CloudStorageSystem {
    private Map<Integer, Integer> users; // user_id -> capacity
    private Map<String, FileEntry> files; // file_name -> FileEntry
    private Map<Integer, Integer> storage; // user_id -> used storage

    public CloudStorageSystem() {
        users = new HashMap<>();
        files = new HashMap<>();
        storage = new HashMap<>();
    }

    public void addUser(int userId, int capacity) {
        if (!users.containsKey(userId)) {
            users.put(userId, capacity);
            storage.put(userId, 0);
        }
    }

    public void addFile(int userId, String fileName, int size) {
        if (users.containsKey(userId) && storage.get(userId) + size <= users.get(userId)) {
            files.put(fileName, new FileEntry(size, userId));
            storage.put(userId, storage.get(userId) + size);
        }
    }

    public List<String> findFile(int userId, String prefix, String suffix) {
        List<String> result = new ArrayList<>();
        for (String file : files.keySet()) {
            if (file.startsWith(prefix) && file.endsWith(suffix)) {
                result.add(file);
            }
        }
        return result;
    }

    public void copyFile(int userId, String source, String destination) {
        if (files.containsKey(source) && files.get(source).getUserId() == userId) {
            int size = files.get(source).getSize();
            if (storage.get(userId) + size <= users.get(userId)) {
                files.put(destination, new FileEntry(size, userId));
                storage.put(userId, storage.get(userId) + size);
            }
        }
    }

    public void compressFile(int userId, String fileName) {
        if (files.containsKey(fileName) && files.get(fileName).getUserId() == userId) {
            FileEntry file = files.get(fileName);
            int oldSize = file.getSize();
            int newSize = oldSize / 2; // halve size
            int freed = oldSize - newSize;

            file.setSize(newSize);
            storage.put(userId, storage.get(userId) - freed);

            System.out.println("Compressed " + fileName + " from " + oldSize + " to " + newSize);
        }
    }

    public void decompressFile(int userId, String fileName) {
        if (files.containsKey(fileName) && files.get(fileName).getUserId() == userId) {
            FileEntry file = files.get(fileName);
            int oldSize = file.getSize();
            int newSize = oldSize * 2; // double size
            int extra = newSize - oldSize;

            if (storage.get(userId) + extra <= users.get(userId)) {
                file.setSize(newSize);
                storage.put(userId, storage.get(userId) + extra);
                System.out.println("Decompressed " + fileName + " from " + oldSize + " to " + newSize);
            } else {
                System.out.println("Not enough capacity to decompress " + fileName);
            }
        }
    }

    // Inner class for file metadata
    private static class FileEntry {
        private int size;
        private int userId;

        public FileEntry(int size, int userId) {
            this.size = size;
            this.userId = userId;
        }

        public int getSize() {
            return size;
        }

        public int getUserId() {
            return userId;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    // Demo main method
    public static void main(String[] args) {
        CloudStorageSystem system = new CloudStorageSystem();
        String[] commands = { "ADD_USER 1 1000", "ADD_FILE 1 file1 100", "FIND_FILE 1 fil .*",
                "COPY_FILE 1 file1 file1_copy", "COMPRESS_FILE 1 file1_copy", "DECOMPRESS_FILE 1 file1_copy" };

        for (String command : commands) {
            String[] parts = command.split(" ");
            String action = parts[0];
            switch (action) {
            case "ADD_USER":
                system.addUser(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                break;
            case "ADD_FILE":
                system.addFile(Integer.parseInt(parts[1]), parts[2], Integer.parseInt(parts[3]));
                break;
            case "FIND_FILE":
                System.out.println(system.findFile(Integer.parseInt(parts[1]), parts[2], parts[3]));
                break;
            case "COPY_FILE":
                system.copyFile(Integer.parseInt(parts[1]), parts[2], parts[3]);
                break;
            case "COMPRESS_FILE":
                system.compressFile(Integer.parseInt(parts[1]), parts[2]);
                break;
            case "DECOMPRESS_FILE":
                system.decompressFile(Integer.parseInt(parts[1]), parts[2]);
                break;
            }
        }
    }
}

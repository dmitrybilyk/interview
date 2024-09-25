package com.conduct.interview._7_patterns.behavioral.iterator;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class IteratorPatternCheck3 {
  public static void main(String[] args) {
    Device device = new Device("device 1");
    Device device2 = new Device("device 2");
    Device device3 = new Device("device 3");
    Device device33 = new Device("device 33");
    Device device5 = new Device("device 5");
    Device device37 = new Device("device 37");
    Device device9 = new Device("device 9");
    //        Device device4 = new Device("device 4");
    DeviceCollection deviceCollection = new DeviceCollectionImpl();
    deviceCollection.addDevice(device);
    deviceCollection.addDevice(device2);
    deviceCollection.addDevice(device3);
    deviceCollection.addDevice(device33);
    deviceCollection.addDevice(device5);
    deviceCollection.addDevice(device37);
    deviceCollection.addDevice(device9);
    //        deviceCollection.addDevice(device4);

    java.util.Iterator deviceIterator = deviceCollection.searchByIterator("5");

    while (deviceIterator.hasNext()) {
      System.out.println(deviceIterator.next());
    }
  }
}

@Getter
@Setter
@AllArgsConstructor
@ToString
class Device {
  private String name;
}

interface DeviceCollection {
  void addDevice(Device device);

  java.util.Iterator iterator();

  java.util.Iterator iterator3();

  java.util.Iterator searchByIterator(String filter);
}

class DeviceCollectionImpl implements DeviceCollection {
  private static final int SIZE_LIMIT = 10;
  private final List<Device> deviceList = new ArrayList<>();

  @Override
  public void addDevice(Device device) {
    if (deviceList.size() >= SIZE_LIMIT) {
      throw new RuntimeException("Collection limit exceeded");
    }
    deviceList.add(device);
  }

  @Override
  public java.util.Iterator iterator() {
    return new DeviceAllIteratorImpl();
  }

  @Override
  public java.util.Iterator iterator3() {
    return new DeviceWith3IteratorImpl();
  }

  @Override
  public java.util.Iterator searchByIterator(String filter) {
    return new DeviceSearchByIteratorImpl(filter);
  }

  private class DeviceAllIteratorImpl implements java.util.Iterator {
    int position = 0;

    @Override
    public Device next() {
      Device device = deviceList.get(position);
      position++;
      return device;
    }

    @Override
    public boolean hasNext() {
      return position < deviceList.size();
    }
  }

  private class DeviceWith3IteratorImpl implements java.util.Iterator {
    int position = 0;

    @Override
    public Device next() {
      for (int i = position; i < deviceList.size(); i++) {
        if (deviceList.get(i).getName().contains(String.valueOf(3))) {
          position = i + 1;
          return deviceList.get(i);
        }
      }
      return null;
    }

    @Override
    public boolean hasNext() {
      if (position == deviceList.size() - 1) {
        return false;
      }
      for (int i = position; i < deviceList.size(); i++) {
        if (deviceList.get(i).getName().contains(String.valueOf(3))) {
          return true;
        }
      }
      return false;
    }
  }

  private class DeviceSearchByIteratorImpl implements java.util.Iterator {
    String filter;
    int position = 0;

    public DeviceSearchByIteratorImpl(String filter) {
      this.filter = filter;
    }

    @Override
    public Device next() {
      for (int i = position; i < deviceList.size(); i++) {
        if (deviceList.get(i).getName().contains(String.valueOf(filter))) {
          position = i + 1;
          return deviceList.get(i);
        }
      }
      return null;
    }

    @Override
    public boolean hasNext() {
      if (position == deviceList.size() - 1) {
        return false;
      }
      for (int i = position; i < deviceList.size(); i++) {
        if (deviceList.get(i).getName().contains(String.valueOf(filter))) {
          return true;
        }
      }
      return false;
    }
  }
}

// interface Iterator {
//    Device next();
//    boolean hasNext();
// }

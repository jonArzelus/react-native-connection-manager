
# react-native-connection-manager

## Getting started

`$ npm install react-native-connection-manager --save`

### Mostly automatic installation

`$ react-native link react-native-connection-manager`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-connection-manager` and add `RNConnectionManager.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNConnectionManager.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNConnectionManagerPackage;` to the imports at the top of the file
  - Add `new RNConnectionManagerPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-connection-manager'
  	project(':react-native-connection-manager').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-connection-manager/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-connection-manager')
  	```

## Usage
```javascript
import RNConnectionManager from 'react-native-connection-manager';
```
  
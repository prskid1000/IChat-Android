import 'dart:async';
import 'dart:convert';

import 'package:IChat/services/services.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;

class Store extends ChangeNotifier {
  final Services scv = Services();

  String theme = 'dark';
  int selectedIndex = 0;

  String userId = "";
  String password = "";
  bool authenticated = false;

  List<String> boxIds = [];
  List<String> users = [];

  bool validUser = false;
  List<String> author = [];
  List<String> message = [];
  String chatBox = "";

  var oneSec = const Duration(seconds: 10);

  Timer chatTimer, userTimer;

  void setTimer() {
    chatTimer = new Timer.periodic(oneSec, (Timer t) => {syncBox(chatBox)});
    userTimer = new Timer.periodic(oneSec, (Timer t) => {syncUser()});
  }

  void stopTimer() {
    chatTimer.cancel();
    userTimer.cancel();
  }

  void toggleTheme(context) {
    if (this.theme.compareTo('dark') == 0) {
      this.theme = 'light';
    } else {
      this.theme = 'dark';
    }
    ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Theme Changed to ' + this.theme)));
    notifyListeners();
  }

  void navigate(int data, BuildContext context) async {
    switch (data) {
      case 0:
        Navigator.pushNamedAndRemoveUntil(context, "Home", (r) => false);
        break;
      case 1:
        Navigator.pushNamedAndRemoveUntil(context, "Contact", (r) => false);
        break;
      case 2:
        Navigator.pushNamedAndRemoveUntil(context, "Chat", (r) => false);
        break;
      case 3:
        stopTimer();
        Navigator.pushNamedAndRemoveUntil(context, "Account", (r) => false);
        break;
      case 4:
        stopTimer();
        SystemNavigator.pop();
        break;
    }
    this.selectedIndex = data;
  }

  Future isAuth() async {
    if (await scv.isAuth(this.userId, this.password) == true) {
      this.authenticated = true;
      return true;
    } else {
      return false;
    }
  }

  Future syncUser() async {
    this.userId = userId;
    this.password = password;
    var url = Uri.https(dotenv.env['SERVER'], '/isauth');
    var response =
        await http.post(url, body: {'userid': userId, 'password': password});
    var decoded = json.decode(response.body);
    if (decoded['success'].toString().compareTo("True") == 0) {
      this.validUser = true;
      this.boxIds.clear();
      for (int i = 0; i < decoded['data']['boxid'].length; i++) {
        this.boxIds.add(decoded['data']['boxid'][i]);
      }
    }

    url = Uri.https(dotenv.env['SERVER'], '/getusers');
    response = await http.get(url);
    decoded = json.decode(response.body)['data'];

    this.users = [];

    for (int i = 0; i < decoded.length; i++) {
      if (this.userId != decoded[i]['userid'])
        this.users.add(decoded[i]['userid']);
    }

    print(this.users);

    notifyListeners();
  }

  Future addUser() async {
    var url = Uri.https(dotenv.env['SERVER'], '/adduser');
    var response =
        await http.post(url, body: {'userid': userId, 'password': password});
    var decoded = json.decode(response.body);
    if (decoded['success'].toString().compareTo("True") == 0) {
      this.validUser = true;
      this.boxIds.clear();
      for (int i = 0; i < decoded['data']['boxid'].length; i++) {
        this.boxIds.add(decoded['data']['boxid'][i]);
      }
    }
    notifyListeners();
  }

  Future comment(BuildContext context, String data) async {
    var url = Uri.https(dotenv.env['SERVER'], '/sendbox');
    var response = await http
        .post(url, body: {'boxid': chatBox, 'userid': userId, 'message': data});
    var decoded = json.decode(response.body)['data']['chat'];

    url = Uri.https(dotenv.env['SERVER'], '/getusers');
    response = await http.get(url);
    decoded = json.decode(response.body)['data'];

    this.users = [];

    for (int i = 0; i < decoded.length; i++) {
      if (this.userId == decoded[i]['userid'])
        this.users.add(decoded[i]['userid']);
    }

    await syncBox(this.chatBox);
    Navigator.pushNamedAndRemoveUntil(context, "Chat", (r) => false);
    notifyListeners();
  }

  Future newBox(BuildContext context, String user) async {
    this.chatBox = "#" + this.userId + "-" + user;

    var url = Uri.https(dotenv.env['SERVER'], '/setbox');
    var response =
        await http.post(url, body: {'boxid': chatBox, 'userid': userId});

    url = Uri.https(dotenv.env['SERVER'], '/setbox');
    response = await http.post(url, body: {'boxid': chatBox, 'userid': user});

    url = Uri.https(dotenv.env['SERVER'], '/sendbox');
    response = await http.post(url,
        body: {'boxid': chatBox, 'userid': userId, 'message': "Hi, " + user});

    Navigator.pushNamedAndRemoveUntil(context, "Chat", (r) => false);
    notifyListeners();
  }

  Future syncBox(String id) async {
    var url = Uri.https(dotenv.env['SERVER'], '/getbox');
    var response = await http.post(url, body: {'boxid': id});
    print(response.body);

    if (json.decode(response.body)['success'].compareTo("True") == 0) {
      var decoded = json.decode(response.body)['data']['chat'];

      this.author = [];
      this.message = [];

      for (int i = 0; i < decoded.length; i++) {
        this.author.insert(0, decoded[i]['author']);
        this.message.insert(0, decoded[i]['message']);
      }
      notifyListeners();
    }
  }

  void deleteBox(BuildContext context, String id) async {
    var url = Uri.https(dotenv.env['SERVER'], '/deletebox');
    var response = await http.post(url, body: {'boxid': id});

    url = Uri.https(dotenv.env['SERVER'], '/unsetbox');
    response = await http.post(url, body: {'userid': this.userId, 'boxid': id});

    String user2 = id.substring(id.indexOf('-') + 1);

    url = Uri.https(dotenv.env['SERVER'], '/unsetbox');
    response = await http.post(url, body: {'userid': user2, 'boxid': id});

    this.boxIds.remove(id);
    notifyListeners();
  }

  void boxTap(BuildContext context, String id) async {
    this.selectedIndex = 2;
    this.chatBox = id;
    await syncBox(id);
    notifyListeners();
    Navigator.pushNamedAndRemoveUntil(context, "Chat", (r) => false);
  }

  List<Widget> messageBuilder(BuildContext context) {
    List<Widget> wid = [];
    for (int i = 0; i < this.boxIds.length; i++) {
      wid.add(Card(
        child: ListTile(
            leading: Icon(Icons.message, color: Colors.black),
            title: Text(boxIds[i],
                style: TextStyle(fontSize: 20, color: Colors.black)),
            contentPadding: EdgeInsets.fromLTRB(10, 0, 10, 0),
            hoverColor: Colors.greenAccent,
            tileColor: Colors.green[300],
            onTap: () {
              boxTap(context, boxIds[i]);
            },
            trailing: InkWell(
              child: Icon(Icons.delete, color: Colors.redAccent),
              onTap: () {
                deleteBox(context, boxIds[i]);
              },
            )),
      ));
    }
    return wid;
  }

  List<Widget> contactBuilder(BuildContext context) {
    List<Widget> wid = [];
    for (int i = 0; i < this.users.length; i++) {
      wid.add(Card(
        child: ListTile(
            leading: Icon(Icons.contacts, color: Colors.black),
            title: Text(users[i],
                style: TextStyle(fontSize: 20, color: Colors.black)),
            contentPadding: EdgeInsets.fromLTRB(10, 0, 10, 0),
            hoverColor: Colors.greenAccent,
            tileColor: Colors.green[300],
            trailing: InkWell(
              child: Icon(
                Icons.chat,
                color: Colors.redAccent,
              ),
              onTap: () {
                newBox(context, users[i]);
              },
            )),
      ));
    }
    return wid;
  }

  List<Widget> chatBuilder(BuildContext context) {
    List<Widget> wid = [];
    for (int i = 0; i < this.author.length; i++) {
      if (this.author[i] != this.userId) {
        wid.add(
          Card(
              margin: EdgeInsets.fromLTRB(0, 5, 100, 5),
              child: Container(
                padding: EdgeInsets.fromLTRB(10, 10, 10, 10),
                child: Column(
                  children: <Widget>[
                    Row(
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: <Widget>[
                        Text(
                          this.author[i],
                          style: TextStyle(fontSize: 18, color: Colors.green),
                        ),
                      ],
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: <Widget>[
                        Flexible(
                          child: TextButton(
                            child: Text(
                              this.message[i],
                              style: TextStyle(
                                  fontSize: 14,
                                  color: theme.compareTo('dark') == 0
                                      ? Colors.white
                                      : Colors.black),
                            ),
                          ),
                        )
                      ],
                    ),
                  ],
                ),
              )),
        );
      } else {
        wid.add(
          Card(
              margin: EdgeInsets.fromLTRB(100, 5, 0, 5),
              child: Container(
                padding: EdgeInsets.fromLTRB(10, 10, 10, 10),
                child: Column(
                  children: <Widget>[
                    Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: <Widget>[
                        Text(
                          this.author[i],
                          style: TextStyle(fontSize: 18, color: Colors.green),
                        ),
                      ],
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: <Widget>[
                        Flexible(
                          child: TextButton(
                            child: Text(this.message[i],
                                style: TextStyle(
                                    fontSize: 14,
                                    color: theme.compareTo('dark') == 0
                                        ? Colors.white
                                        : Colors.black)),
                          ),
                        )
                      ],
                    ),
                  ],
                ),
              )),
        );
      }
    }
    return wid.reversed.toList();
  }
}

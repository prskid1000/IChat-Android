import 'package:IChat/components/frame.dart';
import 'package:IChat/store/store.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class Chat extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    double height = MediaQuery.of(context).size.height;
    double width = MediaQuery.of(context).size.width;
    return Consumer<Store>(
      builder: (context, store, child) {
        return Frame(<Widget>[
          Container(
            height: height * 0.65,
            padding: EdgeInsets.fromLTRB(10, 0, 10, 0),
            child: ListView(
              children: store.chatBuilder(context),
            ),
          ),
          Container(
            padding: EdgeInsets.all(10),
            child: Row(
              children: [
                Container(
                  width: width * 0.70,
                  child: TextFormField(
                    decoration: InputDecoration(
                      border: OutlineInputBorder(
                          borderRadius:
                              BorderRadius.all(Radius.circular(50.0))),
                      labelText: 'Message',
                    ),
                  ),
                ),
                Container(
                    padding: EdgeInsets.fromLTRB(10, 10, 10, 10),
                    child: InkWell(
                      child: Icon(
                        Icons.send,
                        color: Colors.green,
                        size: 50,
                      ),
                      onTap: () {
                        store.comment(context, "");
                      },
                    )),
              ],
            ),
          )
        ]);
      },
    );
  }
}

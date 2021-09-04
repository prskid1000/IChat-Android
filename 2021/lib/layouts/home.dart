import 'package:IChat/components/frame.dart';
import 'package:IChat/store/store.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class Home extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Consumer<Store>(
      builder: (context, store, child) {
        return Frame(store.messageBuilder(context));
      },
    );
  }
}
